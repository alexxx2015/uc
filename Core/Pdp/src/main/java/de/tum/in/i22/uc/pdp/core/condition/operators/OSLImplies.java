package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ImpliesType;

public class OSLImplies extends ImpliesType {
	private static Logger log = LoggerFactory.getLogger(OSLImplies.class);

	public OSLImplies() {
	}

	public OSLImplies(Operator operand1, Operator operand2) {
		operators.add(operand1);
		operators.add(operand2);
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) operators.get(0)).initOperatorForMechanism(mech);
		((Operator) operators.get(1)).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "(" + operators.get(0) + "  ==> " + operators.get(1) + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		Boolean op1state = ((Operator) operators.get(0)).evaluate(curEvent);
		Boolean op2state = ((Operator) operators.get(1)).evaluate(curEvent);
		_state.value = !op1state || op2state;
		log.debug("eval IMPLIES [{}]", _state.value);
		return _state.value;
	}
}
