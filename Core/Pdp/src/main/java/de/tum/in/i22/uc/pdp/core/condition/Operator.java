package de.tum.in.i22.uc.pdp.core.condition;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.shared.Event;

public abstract class Operator {
	protected PolicyDecisionPoint _pdp;
	public OperatorState _state = new OperatorState();

	public void initOperatorForMechanism(Mechanism mech) {
		if (_pdp == null) {
			_pdp = mech.getPolicyDecisionPoint();
		}
	}

	public boolean evaluate(Event curEvent) {
		return false;
	}
}
