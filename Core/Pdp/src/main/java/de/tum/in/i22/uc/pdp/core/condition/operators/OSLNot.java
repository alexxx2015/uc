package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.NotType;

public class OSLNot extends NotType {
	private static Logger log = LoggerFactory.getLogger(OSLNot.class);

	public OSLNot() {
	}

	public OSLNot(Operator operand) {
		operators = operand;
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		((Operator) operators).init(mech);
	}

	@Override
	public String toString() {
		return "! " + operators;
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_state.value = !((Operator) operators).evaluate(curEvent);
		log.debug("eval NOT [{}]", _state.value);
		return _state.value;
	}
}
