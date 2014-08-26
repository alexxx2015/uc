package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ImpliesType;

public class OSLImplies extends ImpliesType {
	private static Logger _logger = LoggerFactory.getLogger(OSLImplies.class);

	private Operator op1;
	private Operator op2;

	public OSLImplies() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech);
		op2.init(mech);
	}

	@Override
	int initId(int id) {
		_id = op1.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return op2.initId(_id);
	}

	@Override
	public String toString() {
		return "(" + op1 + "  ==> " + op2 + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		/*
		 * Important: _Always_ evaluate both operators
		 */
		boolean op1state = op1.evaluate(curEvent);
		boolean op2state = op2.evaluate(curEvent);
		_state.value = !op1state || op2state;
		_logger.debug("eval IMPLIES [{}]", _state.value);
		return _state.value;
	}
}
