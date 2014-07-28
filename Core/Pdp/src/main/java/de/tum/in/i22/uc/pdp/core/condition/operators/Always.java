package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger _logger = LoggerFactory.getLogger(Always.class);

	public Always() {
	}

	public Always(Operator operand1) {
		this.operators = operand1;
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "ALWAYS (" + operators + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		if (!_state.immutable) {
			_state.value = ((Operator) operators).evaluate(curEvent);
			// FIXME: I do not understand why curEvent == null  ... -FK-
			if (!_state.value && curEvent == null) {
				_logger.debug("evaluating ALWAYS: activating IMMUTABILITY");
				_state.immutable = true;
			}
		}
		_logger.debug("eval ALWAYS [{}]", _state.value);
		return _state.value;
	}
}
