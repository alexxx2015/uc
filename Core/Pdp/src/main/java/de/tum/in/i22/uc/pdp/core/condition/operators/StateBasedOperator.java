package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType {

	public StateBasedOperator() {
	}


	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("operator", operator)
				.add("param1", param1)
				.add("param2", param2)
				.add("param3", param3)
				.toString();
	}

	@Override
	public boolean evaluate(IEvent curEvent) {

		IPdp2Pip pip = _pdp.getPip();
		String separator = Settings.getInstance().getSeparator1();

		String p = operator + separator + param1 + separator + param2 + separator + param3;

		return curEvent == null ? pip.evaluatePredicateCurrentState(p) : pip.evaluatePredicateSimulatingNextState(
				curEvent, p);
	}
}
