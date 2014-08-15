package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.OrType;

public class OSLOr extends OrType {
	private static Logger log = LoggerFactory.getLogger(OSLOr.class);

	public OSLOr() {
	}

	public OSLOr(Operator operand1, Operator operand2) {
		operators.add(operand1);
		operators.add(operand2);
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		((Operator) operators.get(0)).init(mech);
		((Operator) operators.get(1)).init(mech);
	}

	@Override
	public String toString() {
		return "(" + operators.get(0) + " || " + operators.get(1) + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		Boolean op1state = ((Operator) operators.get(0)).evaluate(curEvent);
		Boolean op2state = ((Operator) operators.get(1)).evaluate(curEvent);
		_state.value = op1state || op2state;
		log.debug("eval OR [{}]", _state.value);
		return _state.value;
	}
}
