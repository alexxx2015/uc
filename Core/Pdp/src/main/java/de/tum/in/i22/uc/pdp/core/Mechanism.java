package de.tum.in.i22.uc.pdp.core;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;

public abstract class Mechanism extends Observable implements Runnable, IMechanism {
	protected static Logger _logger = LoggerFactory.getLogger(Mechanism.class);

	public static final Object END_OF_TIMESTEP = new Object();

	/**
	 * The name of this {@link Mechanism}.
	 */
	private final String _name;

	private Thread _thisThread;

	/**
	 * A description of this {@link Mechanism}.
	 */
	private final String _description;
	private long _lastTick = 0;
	private long _timestepSize = 0;
	private long _timestep = 0;
	private final EventMatch _triggerEvent;
	private Condition _condition;
	protected AuthorizationAction _authorizationAction;
	private final List<ExecuteAction> _executeAsyncActions;
	private final PolicyDecisionPoint _pdp;

	/**
	 * The point in time in which the very first tick() of this
	 * mechanism took place. This is to synchronize tick()ing
	 * of this Mechanism in a distributed setup.
	 */
	private long _firstTick;

	private final Object _pausedLock = new Object();

	private AtomicBoolean _interrupted = new AtomicBoolean(false);
	private AtomicBoolean _paused = new AtomicBoolean(false);

	private final Deque<Condition> _backupCondition;

	/**
	 * The name of the policy to which this {@link Mechanism} belongs.
	 */
	private final String _policyName;

	protected Mechanism(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp) throws InvalidMechanismException {
		_logger.debug("Preparing mechanism from MechanismBaseType");

		if (pdp == null) {
			_logger.error("Impossible to take proper decision with a null pdp. failing miserably");
			throw new RuntimeException();
		}

		_pdp = pdp;
		_policyName = policyName;

		_name = mech.getName();
		_description = mech.getDescription();
		_timestepSize = mech.getTimestep().getAmount() * TimeAmount.getTimeUnitMultiplier(mech.getTimestep().getUnit());

		_lastTick = 0;
		_timestep = 0;

		_triggerEvent = EventMatch.convertFrom(mech.getTrigger(), _pdp);

		_condition = new Condition(mech.getCondition(), this);

		_backupCondition = new ArrayDeque<>(2);

		_executeAsyncActions = new LinkedList<>();
		for (ExecuteAsyncActionType execAction : mech.getExecuteAsyncAction()) {
			_executeAsyncActions.add(new ExecuteAction(execAction));
		}

		_firstTick = _pdp.getDistributionManager().getFirstTick(_policyName, _name);

		// We will be observed by the PDP, such that we can signal the end of a timestep
		addObserver(pdp);
	}

	@Override
	public String getName() {
		return _name;
	}

	public AuthorizationAction getAuthorizationAction() {
		return _authorizationAction;
	}

	public List<ExecuteAction> getExecuteAsyncActions() {
		return _executeAsyncActions;
	}

	public EventMatch getTriggerEvent() {
		return _triggerEvent;
	}

	@Override
	public long getTimestepSize() {
		return _timestepSize;
	}

	@Override
	public long getLastTick() {
		return _lastTick;
	}

	@Override
	public ICondition getCondition() {
		return _condition;
	}

	@Override
	public String getPolicyName() {
		return _policyName;
	}

	public void revoke() {
		_interrupted.set(true);
	}

	public Decision notifyEvent(IEvent event, Decision d) {
		_logger.debug("Processing mechanism [{}] with event [{}].", _name, event);

		if (_triggerEvent.matches(event)) {
			_logger.info("Trigger event matches. Evaluating condition");

			if (evaluateCondition(false)) {
				_logger.info("Condition satisfied; merging mechanism into decision");
				d.processMechanism(this, event);
			} else {
				_logger.info("Condition NOT satisfied");
			}
		}

		return d;
	}

	@Override
	public void startSimulation() {
		_condition.startSimulation();
		_backupCondition.addFirst(_condition);
	}

	@Override
	public void stopSimulation() {
		if (_backupCondition.isEmpty()) {
			throw new IllegalStateException("No ongoing simulation. Cannot stop simulation.");
		}
		_condition = _backupCondition.getFirst();
		_condition.stopSimulation();
	}

	@Override
	public boolean isSimulating() {
		return !_backupCondition.isEmpty();
	}

	private boolean tick() {

		_timestep++;
		_logger.debug("//////////////////////////////////////////////////////");
		_logger.debug("[{}] Ticking. Timestep no. {}. Next tick in {}us", _name, _timestep, _timestepSize);


		boolean conditionValue = evaluateCondition(true);
		_logger.debug("Condition evaluated to: " + conditionValue);
		_logger.debug("//////////////////////////////////////////////////////");

		return conditionValue;
	}

	/**
	 * Evaluate the condition of this mechanism.
	 * Important: Make the method call synchronous,
	 * because both tick() and
	 * {@link Mechanism#notifyEvent(IEvent, Decision)}
	 * might call this method.
	 *
	 * @return the result of the condition evaluation.
	 */
	private synchronized boolean evaluateCondition(boolean endOfTimestep) {
		return _condition.tick(endOfTimestep);
	}

	/**
	 * Makes this thread sleep for the specified amount of
	 * milliseconds. The method returns true, if the sleep
	 * was successful, i.e. if it was not interrupted.
	 *
	 * This sleep() might get interrupted due to a call
	 * to pause().
	 *
	 * 2014/11/07 FK
	 *
	 * @param millis the number of milliseconds
	 * @return true, if the sleep was not interrupted.
	 */
	private boolean sleep(long millis) {
		if (millis <= 0) {
			return true;
		}
		try {
			_logger.debug("Sleeping {}ms (out of requested {}ms).", millis, _timestepSize);
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			if (!_paused.get()) {
				_interrupted.set(true);
			}
			return false;
		}

		return true;
	}

	private void initializeLastTick() {
		if (_firstTick == Long.MIN_VALUE) {
			/*
			 * Artificial point in time at which the last tick
			 * supposedly 'happened'. Just to get things rolling...
			 */
			_lastTick = System.currentTimeMillis() - _timestepSize;
			_firstTick = _lastTick;

			/*
			 * Also register the Mechanism with the DistributionManager
			 * such that other systems will synchronize on the
			 * value of _firstTick.
			 */
			new Thread() {
				@Override
				public void run() {
					_pdp.getDistributionManager().setFirstTick(_policyName, _name, _firstTick);
				};
			}.start();
		}
		else {
			/*
			 * Calculate the value of _lastTick on the basis
			 * of _firstTick and _timestepSize.
			 */
			long now = System.currentTimeMillis();
			_lastTick = now - (now - _firstTick) % _timestepSize;
		}
	}

	/**
	 * Main run method. Rewritten on 2014/11/10.
	 *
	 * The old implementation of this run() method was wrong.
	 * The overhead for performing the policy evaluation was not
	 * considered in the sleep()s in-between policy evaluations.
	 * The result was that the evaluated time interval and the
	 * actual system time drifted further and further. Tests
	 * revealed that this new implementation is not subject to
	 * such a phenomenon. The (im)precision of this new implementation
	 * (with debug output enabled) is roughly 1 (one) millisecond.
	 *
	 * 2014/11/10 FK
	 */
	@Override
	public void run() {
		_logger.info("Starting mechanism tick thread ({}ms).", _timestepSize);

		initializeLastTick();

		while (!_interrupted.get()) {

			/*
			 * Calculate the relative amount of time (in milliseconds)
			 * that has passed during the last iteration. The result
			 * is the amount of time that passed during the last tick()
			 * and all associated overhead within this method.
			 */
			if (!sleep(_lastTick + _timestepSize - System.currentTimeMillis())) {
				/*
				 * Sleep was interrupted and returned false.
				 * There are two reasons why this might happen:
				 * (1) The mechanism was revoked.
				 * (2) The mechanism was set to pause.
				 */

				try {
					synchronized (_pausedLock) {
						/*
						 * If the mechanism was paused, wait for
						 * the pause to be released.
						 */
						while (_paused.get()) {
							_pausedLock.wait();
						}
					}
				}
				catch (InterruptedException e) {
					if (!_paused.get()) {
						/*
						 * Once the pause is released, re-initialize
						 * lastTick variable.
						 */
						initializeLastTick();
					}
					/*
					else {
						// In this cases the Mechanism was revoked (_interrupted.get() == true),
						// which will result in termination of this method's main loop.
					}
					*/
				}

				/*
				 * Continue to next loop iteration.
				 */
				continue;
			}

			/*
			 * Perform the actual tick().
			 */
			_logger.info("Ticking at time {}.", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
			if (tick()) {
				_logger.info("Triggering optional executeActions");
				for (final ExecuteAction execAction : getExecuteAsyncActions()) {
					/*
					 * Being asynchronous execute actions, their
					 * execution can be parallelized.
					 */
					new Thread() {
						@Override
						public void run() {
							_pdp.executeAction(execAction, false);
						};
					}.start();
				}
			}

			/*
			 *  Adjust lastTick value. Do this _after_
			 *  tick(), because condition evaluation
			 *  will rely on its old value.
			 */
			_lastTick += _timestepSize;

			setChanged();
			notifyObservers(END_OF_TIMESTEP);
		}
	}

	/**
	 * Pauses this mechanism.
	 *
	 * Subsequent unpausing causes an
	 * immediate re-evaluation (i.e. tick())
	 *
	 * 2014/11/07 FK
	 */
	public void pause() {
		synchronized (_pausedLock) {
			_paused.set(true);

			/*
			 *  Send an interrupt to this thread,
			 *  is the mechanism might currently
			 *  be sleeping. We want to wake it up.
			 */
			_thisThread.interrupt();
		}
	}

	/**
	 * Unpauses this mechanism.
	 *
	 * 2014/11/07 FK
	 */
	public void unpause() {
		synchronized (_pausedLock) {
			_paused.set(false);
			_pausedLock.notify();
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this.getClass())
				.add("_name", _name)
				.add("_description", _description)
				.add("_timestepSize", _timestepSize)
				.add("_paused", _paused)
				.add("_firstTick", _firstTick)
				.add("_lastTick", _lastTick)
				.add("_timestep", _timestep)
				.add("_triggerEvent", _triggerEvent)
				.add("_condition", _condition)
				.add("_authorizationAction", _authorizationAction)
				.add("_executeActions", _executeAsyncActions)
				.toString();
	}

	public List<ExecuteAction> getExecuteActions() {
		return _executeAsyncActions;
	}

	public PolicyDecisionPoint getPolicyDecisionPoint() {
		return _pdp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Mechanism) {
			Mechanism other = (Mechanism) obj;
			return Objects.equals(_name, other._name)
					&& Objects.equals(_policyName, other._policyName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _policyName);
	}

	public void setThread(Thread t) {
		_thisThread = t;
	}

	public void setFirstTick(long firstTick) {
		if (_firstTick != Long.MIN_VALUE) {
			throw new IllegalStateException("Cannot set firstTick more than once.");
		}
		_firstTick = firstTick;
	}

	public long getFirstTick() {
		return _firstTick;
	}
}
