package de.tum.in.i22.uc.pdp.core.condition;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public abstract class Operator {
	protected PolicyDecisionPoint _pdp;
	protected OperatorState _state = new OperatorState();

	public void init(Mechanism mech) {
		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
		}
		else {
			throw new UnsupportedOperationException("Operator may only get initialized once.");
		}
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
