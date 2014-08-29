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
	protected OperatorState _state;
	protected DistributedOperatorState _dstate;
	protected IMechanism _mechanism;


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

	public Operator() {
		_state = new OperatorState(this);

		if (Settings.getInstance().getDistributionEnabled()) {
			_dstate = new DistributedOperatorState(this);
		}
	}

	public void init(Mechanism mech) {
		if (mech == null) {
			throw new NullPointerException("Provided mechanism was null.");
		}

		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
			_mechanism = mech;
			this.addObserver(_pdp);
		}
		else {
			throw new UnsupportedOperationException("Operator may only get initialized once.");
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

	int initId(int id) {
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
	public IMechanism getMechanism() {
		return _mechanism;
	}

	@Override
	public final boolean evaluate(IEvent ev) {
		if (Settings.getInstance().getDistributionEnabled()) {
			return distributedEvaluation(ev);
		}
		else {
			return localEvaluation(ev);
		}
	}

	protected boolean localEvaluation(IEvent ev) {
		throw new UnsupportedOperationException("Calling localEvaluation() is only allowed on subtypes of " + Operator.class);
	}

	protected boolean distributedEvaluation(IEvent ev) {
		return localEvaluation(ev);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Operator) {
			return Objects.equals(_fullId, ((Operator) obj)._fullId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_fullId);
	}
}
