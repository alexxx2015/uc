package de.tum.in.i22.uc.pdp.core.condition;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public abstract class Operator {
	protected PolicyDecisionPoint _pdp;
	public OperatorState _state = new OperatorState();

	public void initOperatorForMechanism(Mechanism mech) {
		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
		}
	}

	/**
	 * Evaluates this operator given the specified event.
	 * If the specified event is null, then this is interpreted
	 * as the end of a timestep and this operator is
	 * evaluated accordingly.
	 *
	 * @param curEvent
	 * @return
	 */
	public boolean evaluate(IEvent curEvent) {
		return false;
	}
}
