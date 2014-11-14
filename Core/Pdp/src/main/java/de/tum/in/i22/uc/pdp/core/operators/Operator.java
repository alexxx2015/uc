package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;

public abstract class Operator extends Observable implements IOperator {
	protected static final Logger _logger = LoggerFactory.getLogger(Operator.class);

	protected PolicyDecisionPoint _pdp;
	protected IMechanism _mechanism;

	/**
	 * The parent operator, or null for the root node.
	 */
	private Operator _parent;

	/**
	 *
	 * Time to live for distributed usage control. I.e. long this
	 * {@link Operator}'s state must not be deleted due to later
	 * evaluations.
	 *
	 * Specified in milliseconds.
	 */
	private long _ttl;

	/**
	 * Internal identifier for this {@link IOperator}, assigned
	 * by {@link Operator#setId(int)}; based on {@link Operator#_id}.
	 */
	private String _fullId;

	/**
	 * Internal identifier for this {@link Operator}, assigned
	 * by invoking {@link Operator#initId()} on the root operator
	 * of this {@link ICondition}.
	 */
	private int _id;

	private boolean _initialized;

	private final Deque<State> _backupStates;

	protected State _state;

	/**
	 * In distributed usage control, _positivity
	 * indicates whether for this {@link Operator}
	 * (1) local satisfaction implies global satisfaction ({@link Trilean#TRUE}),
	 * (2) local violation implies global violation ({@link Trilean#FALSE}),
	 * (3) neither of the two above ({@link Trilean#UNDEF}).
	 */
	protected Trilean _positivity = Trilean.UNDEF;

	public Operator() {
		_state = new State();
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
		_initialized = false;
		_backupStates = new ArrayDeque<>(2);
	}

	public final void init(Mechanism mechanism) {
		init(mechanism, null, mechanism.getTimestepSize() * 2);
	}

	protected void init(Mechanism mech, Operator parent, long ttl) {
		if (mech == null) {
			throw new NullPointerException("Provided mechanism was null.");
		}

		if (_initialized) {
			throw new IllegalStateException("Operator may only get initialized once.");
		}

		_initialized = true;

		_pdp = mech.getPolicyDecisionPoint();
		_mechanism = mech;
		_parent = parent;
		_ttl = ttl;

		addObserver(_pdp);
	}

	/**
	 * Initializes and assigns IDs to this {@link Operator} as
	 * well as all {@link Operator}s that are nested within this one.
	 * The ID is a simple integer value. The initialization/assignment
	 * of those IDs is performed in an in-order fashion by traversing
	 * the {@link Operator} tree.
	 */
	public final void initId() {
		initId(0);
	}

	protected int initId(int id) {
		// By default, no ID is set for an Operator, and, subsequently,
		// all of its children
		return id;
	}

	/**
	 * Sets this {@link Operator}'s internal identifier such that the
	 * {@link Operator} can be uniquely identified even if the policy
	 * is sent to another PDP/PMP.
	 *
	 * @param id
	 */
	protected final int setId(int id) {
		_id = id;
		_fullId = _mechanism.getPolicyName() + "#" + _mechanism.getName() + "#" + _id;
		_logger.debug("My [{}] id is {}.", this, _fullId);
		return _id;
	}

	public int getId() {
		return _id;
	}

	@Override
	public final String getFullId() {
		return _fullId;
	}

	@Override
	public final IMechanism getMechanism() {
		return _mechanism;
	}

	public boolean tick(boolean endOfTimestep) {
		throw new UnsupportedOperationException("Calling tick() is only allowed on subtypes of " + Operator.class);
	}

	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Operator) {
			return Objects.equals(_fullId, ((Operator) obj)._fullId);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_fullId);
	}

	/**
	 * Returns the Time to live (TTL) of this {@link Operator}.
	 * The TTL specifies how long _each_ state of this {@link Operator} must not be deleted.
	 * Specified in milliseconds.
	 * @return the time-to-live value of this {@link Operator}.
	 */
	@Override
	public final long getTTL() {
		return _ttl;
	}

	@Override
	public final Trilean getPositivity() {
		return _positivity;
	}

	public void startSimulation() {
		_logger.debug("startSimulation. Storing " + _state);
		_backupStates.addFirst(_state.deepClone());
	}

	public void stopSimulation() {
		if (_backupStates.isEmpty()) {
			throw new IllegalStateException("No ongoing simulation. Cannot stop simulation.");
		}
		_state = _backupStates.getFirst();
		_logger.debug("stopSimulation. Restoring " + _state);
	}

	public final State getState() {
		return _state;
	}

	public Collection<Observer> getObservers(Collection<Observer> observers) {
		return observers;
	}

	@Override
	public boolean getValueAtLastTick() {
		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}
//
//	protected Pair<Long,Long> getFromTo(DistributionGranularity granularity) {
//		long from = 0;
//		long to = 0;
//
//		switch (granularity.getGranularity()) {
//		case EXACT:
//			if (_mechanism.isSimulating()) {
//				from = System.currentTimeMillis() - 1;
//			}
//			else {
//				from = _mechanism.getLastTick() + _mechanism.getTimestepSize() - 1;
//			}
//			to = from + 2;
//			break;
//		case TIMESTEP:
//			from = _mechanism.getLastTick();
//			to = from + _mechanism.getTimestepSize();
//			break;
//		case MILLISECONDS:
//			from = System.currentTimeMillis() - granularity.getMilliseconds();
//			to = from + 2 * granularity.getMilliseconds();
//			break;
//		}
//
//		return Pair.of(from, to);
//	}
}
