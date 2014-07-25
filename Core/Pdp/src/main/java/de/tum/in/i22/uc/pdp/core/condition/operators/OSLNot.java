package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.xsd.NotType;

public class OSLNot extends NotType {
	private static Logger log = LoggerFactory.getLogger(OSLNot.class);

	public OSLNot() {
	}

	public OSLNot(Operator operand) {
		this.operators = operand;
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "! " + this.getOperators();
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		this._state.value = !((Operator) this.getOperators()).evaluate(curEvent);
		log.debug("eval NOT [{}]", this._state.value);
		return this._state.value;
	}
}
