package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.condition.OperatorState;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public abstract class Operator {
	protected PolicyDecisionPoint _pdp;
	protected OperatorState _state = new OperatorState();

	protected int _id;

	public void init(Mechanism mech) {
		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
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
