package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPdpMechanism;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public StateBasedOperator() {
	}

	public StateBasedOperator(StateBasedOperatorType op, Mechanism curMechanism) {
		_logger.debug("Processing StateBasedFormula from StateBasedOperatorType");
		operator = op.getOperator();
		param1 = op.getParam1();
		param2 = op.getParam2();
		param3 = op.getParam3();
	}

	@Override
	public void initOperatorForMechanism(IPdpMechanism mech) {
		super.initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "StateBasedFormula [operator='" + getOperator() + "', param1='" + getParam1() + "', param2='"
				+ getParam2() + "', param3='" + getParam3() + "']";
	}

	@Override
	public boolean evaluate(Event curEvent) {

		IPdp2Pip pip = _pdp.getPip();
		String separator = Settings.getInstance().getSeparator1();

		String p = operator + separator + param1 + separator + param2 + separator + param3;

		return curEvent == null ? pip.evaluatePredicateCurrentState(p) : pip.evaluatePredicateSimulatingNextState(
				curEvent.toIEvent(), p);
	}
}
