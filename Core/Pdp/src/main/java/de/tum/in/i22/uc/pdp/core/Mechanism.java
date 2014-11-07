package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

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
	private long _lastUpdate = 0;
	private long _timestepSize = 0;
	private long _timestep = 0;
	private final EventMatch _triggerEvent;
	private Condition _condition;
	protected AuthorizationAction _authorizationAction;
	private final List<ExecuteAction> _executeAsyncActions;
	private final PolicyDecisionPoint _pdp;
	private boolean _interrupted = false;

	private final Object _pausedLock = new Object();
	private boolean _paused = false;

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
		_lastUpdate = 0;
		_timestepSize = mech.getTimestep().getAmount() * TimeAmount.getTimeUnitMultiplier(mech.getTimestep().getUnit());
		_timestep = 0;
		_triggerEvent = EventMatch.convertFrom(mech.getTrigger(), _pdp);

		_condition = new Condition(mech.getCondition(), this);
		_executeAsyncActions = new LinkedList<ExecuteAction>();

		_backupCondition = new ArrayDeque<>(2);

		_logger.debug("Processing executeAsyncActions");
		// Processing synchronous executeActions for allow
		for (ExecuteAsyncActionType execAction : mech.getExecuteAsyncAction()) {
			_executeAsyncActions.add(new ExecuteAction(execAction));
		}

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
	public long getLastUpdate() {
		return _lastUpdate;
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
		_interrupted = true;
	}

	public Decision notifyEvent(IEvent event, Decision d) {
		_logger.debug("Processing mechanism [{}] with event [{}].", _name, event);

		if (_triggerEvent.matches(event)) {
			_logger.info("Trigger event matches. Evaluating condition");

			if (evaluateCondition()) {
				_logger.info("Condition satisfied; merging mechanism into decision");
				d.processMechanism(this, event);
			} else {
				_logger.info("Condition NOT satisfied");
			}
		}

		return d;
	}

	public void startSimulation() {
		_condition.startSimulation();
		_backupCondition.addFirst(_condition);
	}

	public void stopSimulation() {
		if (_backupCondition.isEmpty()) {
			throw new IllegalStateException("No ongoing simulation. Cannot stop simulation.");
		}
		_condition = _backupCondition.getFirst();
		_condition.stopSimulation();
	}

	private boolean tick() {

		_timestep++;
		_logger.debug("//////////////////////////////////////////////////////");
		_logger.debug("[{}] Ticking. Timestep no. {}. Next tick in {}us", _name, _timestep, _timestepSize);


		boolean conditionValue = evaluateCondition();
		_logger.debug("Condition evaluated to: " + conditionValue);
		_logger.debug("//////////////////////////////////////////////////////");

		setChanged();
		notifyObservers(END_OF_TIMESTEP);

		return conditionValue;
	}

	/**
	 * Evaluate the condition of this mechanism.
	 * Important: Make the method call synchronous,
	 * because both {@link Mechanism#tick()} and
	 * {@link Mechanism#notifyEvent(IEvent, Decision)}
	 * might call this method.
	 *
	 * @return the result of the condition evaluation.
	 */
	private synchronized boolean evaluateCondition() {
		return _condition.tick();
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
			_logger.debug("Sleeping {}ms.", millis);
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			if (!_paused) {
				_interrupted = true;
			}
			return false;
		}

		return true;
	}

	/**
	 * 2014/11/07 FK
	 */
	@Override
	public void run() {
		_logger.info("Starting mechanism update thread usleep={} ms", _timestepSize);

		long now = System.currentTimeMillis();
		long elapsed = _timestepSize;
		_lastUpdate = now - _timestepSize;

		while (!_interrupted) {
			/*
			 * This block is to pause the mechanism in
			 * correspondence with attribute _paused.
			 */
			try {
				synchronized (_pausedLock) {
					while (_paused) {
						_pausedLock.wait();
					}
				}
			}
			catch (InterruptedException e) {
				if (_interrupted) {
					continue;
				}

				/*
				 * Once the pause is released, set those
				 * variables to their initial values such that
				 * an immediate tick() will be executed.
				 */
				now = System.currentTimeMillis();
				elapsed = _timestepSize;
				_lastUpdate = now - _timestepSize;
			}


			if (_lastUpdate >= now) {
				/*
				 * now is now.
				 * By setting _lastUpdate = now + _timestepSize
				 * _after_ the tick(), the time needed for tick()
				 * is attributed to the _old_ timestep, which reflects
				 * the fact that tick() does not consider events
				 * that happen from now on. Therefore, the _next_ tick()
				 * will need to evaluate from timepoint
				 * now + _timestepSize onward. -FK-
				 */
				now = System.currentTimeMillis();

				/*
				 * We know that this is not the first iteration of
				 * this loop and we know that this Mechanism had
				 * not been paused. Therefore, we measure how much time
				 * the last iteration took and adjust the sleep time
				 * accordingly.
				 */
				elapsed = (elapsed - now) * -1;
				if (!sleep(_timestepSize - elapsed)) {
					// If the sleep was interrupted, start all over.
					continue;
				}
			}
			/* else {
				// In this case we know that it's the Mechanisms first iteration
				// or that it had been paused. We do not need to do anything, because
				// we want to perform an immediate evaluation.
			}*/

			/*
			 * We measure the actual execution/evaluation time of one tick().
			 * This is important, because we need to subtract the measured time
			 * from the next Thread.sleep() (as done above). Otherwise we will keep shifting
			 * the evaluation for the amount of time that has elapsed during policy
			 * evaluation.
			 * This measurement is started now and ends upon the next loop
			 * right above. This way, the measurement is as exact as possible
			 * -FK-
			 */
			elapsed = now;

			if (tick()) {
				_logger.info("Triggering optional executeActions");
				for (ExecuteAction execAction : getExecuteAsyncActions()) {
					_pdp.executeAction(execAction, false);
				}
			}

			_lastUpdate = now + _timestepSize;
		}

		_logger.info("Mechanism [{}] was stopped.", _name);
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
			_paused = true;

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
	 * Unpausing causes an
	 * immediate re-evaluation (i.e. tick())
	 *
	 * 2014/11/07 FK
	 */
	public void unpause() {
		synchronized (_pausedLock) {
			_paused = false;
			_pausedLock.notify();
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this.getClass())
				.add("_name", _name)
				.add("_description", _description)
				.add("_timestepSize", _timestepSize)
				.add("_lastUpdate", _lastUpdate)
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
}
