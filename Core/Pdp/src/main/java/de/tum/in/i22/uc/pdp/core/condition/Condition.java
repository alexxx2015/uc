package de.tum.in.i22.uc.pdp.core.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionType;

public class Condition {
	private static Logger _logger = LoggerFactory.getLogger(Condition.class);

	private Operator _operator = null;

	public Condition() {
	}

	public Condition(ConditionType cond, Mechanism curMechanism) {
		_logger.debug("Preparing condition from ConditionType");
		_operator = (Operator) cond.getOperators();

		if (_operator == null) {
			_logger.error("Condition is empty.");
			throw new NullPointerException("Condition is empty.");
		}

		_operator.init(curMechanism);

		_logger.debug("condition: {}", _operator);
	}

	@Override
	public String toString() {
		return "Condition: { " + _operator + " }";
	}

	/**
	 * This method evaluates this {@link Condition} in the presence
	 * of the specified event at the current point in time.
	 * Evaluation takes into account that the specified event is
	 * currently happening. If the specified event is null, then this
	 * {@link Condition} is evaluated as-is. This is useful for evaluating
	 * the condition at the end of a timestep.
	 *
	 * @param event
	 * @return
	 */
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
