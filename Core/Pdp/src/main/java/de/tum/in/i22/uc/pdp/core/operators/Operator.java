package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Objects;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public abstract class Operator extends Observable implements IOperator {
	protected static final Logger _logger = LoggerFactory.getLogger(Operator.class);

	protected PolicyDecisionPoint _pdp;
//	private OperatorState _state;
	protected IMechanism _mechanism;

	protected boolean _valueAtLastTick;

	/**
	 * The parent operator, or null for the root node.
	 */
	private Operator _parent;

	/**
	 * Time to live; i.e. how long _each_ state of this {@link Operator} must not be deleted.
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

	private boolean _isSimulating;

	public Operator() {
		_valueAtLastTick = false;
		_initialized = false;
		_isSimulating = false;
	}

	public final void init(Mechanism mechanism) {
		init(mechanism, null, Long.MIN_VALUE);
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

//		_state = new OperatorState(this);

		if (Settings.getInstance().getDistributionEnabled()) {
			this.addObserver(_pdp);
		}
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

//	@Override
//	public final boolean evaluate(IEvent ev) {
//		boolean result = localEvaluation(ev);
//
//		if (Settings.getInstance().getDistributionEnabled()) {
//			result = distributedEvaluation(result, ev);
//			_logger.debug("distributedEvaluation({}): {}", this, result);
//		}
//		else {
//			_logger.debug("localEvaluation({}): {}", this, result);
//		}
//
//		return result;
//	}
//
//	protected boolean localEvaluation(IEvent ev) {
//		throw new UnsupportedOperationException("Calling localEvaluation() is only allowed on subtypes of " + Operator.class);
//	}
//
//	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
//		return resultLocalEval;
//	}

	public boolean tick() {
		throw new UnsupportedOperationException("Calling tick() is only allowed on subtypes of " + Operator.class);
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
	public final long getTTL() {
		return _ttl;
	}

	public final boolean getValueAtLastTick() {
		return _valueAtLastTick;
	}

	private boolean _backupValueAtLastTick;

	public void startSimulation() {
		if (_isSimulating) {
			throw new IllegalStateException("Already simulating. Nested simulation not yet implemented.");
		}
		_isSimulating = true;
		_backupValueAtLastTick = _valueAtLastTick;
	}

	public void stopSimulation() {
		if (!_isSimulating) {
			throw new IllegalStateException("No ongoing simulation. Cannot stop simulation.");
		}
		_isSimulating = false;
		_valueAtLastTick = _backupValueAtLastTick;
	}

	public boolean isSimulating() {
		return _isSimulating;
	}
}
