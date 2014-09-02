package de.tum.in.i22.uc.pdp.core.condition.operators;

import java.util.Objects;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public abstract class Operator extends Observable implements IOperator {
	protected static final Logger _logger = LoggerFactory.getLogger(Operator.class);

	protected PolicyDecisionPoint _pdp;
//	private OperatorState _state;
	protected IMechanism _mechanism;

	/**
	 * The parent operator, or null for the root node.
	 */
	private Operator _parent;

	/**
	 * Time to live; i.e. how long _each_ state of this {@link Operator} must not be deleted.
	 * Specified in milliseconds.
	 */
	protected long _ttl;

	/**
	 * Internal identifier for this {@link IOperator}, assigned
	 * by {@link Operator#setFullId(int)}; based on {@link Operator#_id}.
	 */
	private String _fullId;

	/**
	 * Internal identifier for this {@link Operator}, assigned
	 * by invoking {@link Operator#initId()} on the root operator
	 * of this {@link ICondition}.
	 */
	protected int _id;

	private boolean _initialized = false;

	public Operator() {
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
	protected final void setFullId(int id) {
		_fullId = _mechanism.getPolicyName() + "#" + _mechanism.getName() + "#" + id;
	}

	@Override
	public final String getFullId() {
		return _fullId;
	}

	@Override
	public final IMechanism getMechanism() {
		return _mechanism;
	}

	@Override
	public final boolean evaluate(IEvent ev) {
		boolean result = localEvaluation(ev);

		if (Settings.getInstance().getDistributionEnabled()) {
			result = distributedEvaluation(result, ev);
			_logger.debug("distributedEvaluation({}): {}", this, result);
		}
		else {
			_logger.debug("localEvaluation({}): {}", this, result);
		}

		return result;
	}

	protected boolean localEvaluation(IEvent ev) {
		throw new UnsupportedOperationException("Calling localEvaluation() is only allowed on subtypes of " + Operator.class);
	}

	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
		return resultLocalEval;
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
}
