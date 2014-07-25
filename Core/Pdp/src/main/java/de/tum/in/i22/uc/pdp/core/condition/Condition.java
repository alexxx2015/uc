package de.tum.in.i22.uc.pdp.core.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionType;

public class Condition {
	private static Logger _logger = LoggerFactory.getLogger(Condition.class);

	public Operator _operator = null;

	public Condition() {
	}

	public Condition(ConditionType cond, Mechanism curMechanism) {
		_logger.debug("Preparing condition from ConditionType");
		_operator = (Operator) cond.getOperators();
		_operator.initOperatorForMechanism(curMechanism);

		// try
		// {
		// Constructor<? extends Object> constructor =
		// cond.getOperators().getClass().getConstructor((cond.getOperators().getClass().getSuperclass()),
		// Mechanism.class);
		// operator =
		// (Operator)constructor.newInstance(cond.getOperators(), curMechanism);
		// log.debug("operator = {}", operator);
		// }
		// catch(NoSuchMethodException | InstantiationException |
		// IllegalAccessException | IllegalArgumentException |
		// InvocationTargetException e)
		// {
		// e.printStackTrace();
		// }
		_logger.debug("condition: {}", _operator);
	}

	@Override
	public String toString() {
		return "Condition: { " + _operator + " }";
	}

	public boolean evaluate(IEvent curEvent) {
		_logger.debug("Evaluating condition...");
		if (_operator == null) {
			_logger.error("condition is empty. evaluates to true. Strange, though. Who writes such mechanisms?");
			return true;
		}

		boolean ret = false;
		try {
			ret = _operator.evaluate(curEvent);
		} catch (Exception e) {
			_logger.error("Exception during evaluation: {}", e.getMessage());
		}
		_logger.debug("condition value: [{}]", ret);
		return ret;
	}

}
