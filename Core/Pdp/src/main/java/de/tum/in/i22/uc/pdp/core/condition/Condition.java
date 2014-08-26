package de.tum.in.i22.uc.pdp.core.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.operators.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionType;

public class Condition implements ICondition {
	private static Logger _logger = LoggerFactory.getLogger(Condition.class);

	private Operator _operator = null;

	public Condition() {
	}

	public Condition(ConditionType cond, Mechanism mechanism) {
		_logger.debug("Preparing condition from ConditionType");
		_operator = (Operator) cond.getOperators();

		if (_operator == null) {
			_logger.error("Condition is empty.");
			throw new NullPointerException("Condition is empty.");
		}

		_operator.init(mechanism);
		_operator.initId();

		_logger.debug("condition: {}", _operator);
	}

	@Override
	public String toString() {
		return "Condition: { " + _operator + " }";
	}

	@Override
	public boolean evaluate(IEvent event) {
		_logger.debug("Evaluating condition...");

		boolean ret = false;
		try {
			ret = _operator.evaluate(event);
		} catch (Exception e) {
			_logger.error("Exception during evaluation: {}", e.getMessage());
		}

		_logger.debug("condition value: [{}]", ret);
		return ret;
	}
}
