package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AndType;

public class OSLAnd extends AndType {
	private static Logger _logger = LoggerFactory.getLogger(OSLAnd.class);

	public OSLAnd() {
	}

	public OSLAnd(Operator operand1, Operator operand2) {
		this.getOperators().add(operand1);
		this.getOperators().add(operand2);
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		((Operator) this.getOperators().get(0)).init(mech);
		((Operator) this.getOperators().get(1)).init(mech);
	}

	@Override
	public String toString() {
		return this.getOperators().get(0) + " && " + this.getOperators().get(1);
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		/*
		 * Important: _Always_ evaluate both operators
		 */
		boolean op1state = ((Operator) this.getOperators().get(0)).evaluate(curEvent);
		boolean op2state = ((Operator) this.getOperators().get(1)).evaluate(curEvent);
		this._state.value = op1state && op2state;
		_logger.debug("eval AND [{}]", this._state.value);
		return this._state.value;
	}
}
