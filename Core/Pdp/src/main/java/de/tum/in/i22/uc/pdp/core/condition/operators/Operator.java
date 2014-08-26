package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.condition.Condition;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public abstract class Operator {
	protected PolicyDecisionPoint _pdp;
	protected OperatorState _state;
	protected Mechanism _mechanism;

	/**
	 * Internal identifier for this {@link Operator}, assigned
	 * by {@link Operator#setFullId(int)}; based on {@link Operator#_id}.
	 */
	private String _fullId;

	/**
	 * Internal identifier for this {@link Operator}, assigned
	 * by invoking {@link Operator#initId()} on the root operator
	 * of this {@link Condition}.
	 */
	protected int _id;

	public Operator() {
		_state = new OperatorState();
	}

	public void init(Mechanism mech) {
		if (mech == null) {
			throw new NullPointerException("Provided mechanism was null.");
		}

		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
			_mechanism = mech;
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
		throw new UnsupportedOperationException("Calling initId() is only allowed on subtypes of " + Operator.class + " (was: " + getClass() + ")");
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

	/**
	 * Returns this {@link Operator}'s internal identifier as a string.
	 * @return this {@link Operator}'s internal identifier as a string.
	 */
	public final String getFullId() {
		return _fullId;
	}

	/**
	 * Evaluates this operator given the specified event.
	 * If the specified event is null, then this is interpreted
	 * as the end of a timestep and this {@link Operator} is
	 * evaluated accordingly.
	 *
	 * This method is only to be called on subtypes of this class.
	 * Otherwise, a {@link UnsupportedOperationException} will be thrown.
	 *
	 * @param curEvent
	 * @return
	 */
	public boolean evaluate(IEvent curEvent) {
		throw new UnsupportedOperationException("Calling evaluate() is only allowed on subtypes of " + Operator.class);
	}
}
