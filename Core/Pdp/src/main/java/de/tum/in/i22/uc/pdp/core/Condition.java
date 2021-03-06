package de.tum.in.i22.uc.pdp.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidConditionException;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.Operator;
import de.tum.in.i22.uc.pdp.xsd.ConditionType;

public class Condition implements ICondition {
	private static Logger _logger = LoggerFactory.getLogger(Condition.class);

	private Operator _operator = null;

	public Condition() {
	}

	public Condition(ConditionType cond, Mechanism mechanism) throws InvalidConditionException {
		_operator = (Operator) cond.getOperators();

		if (_operator == null) {
			throw new InvalidConditionException("Condition is empty.");
		}

		try {
			_operator.init(mechanism);
		}
		catch (InvalidOperatorException e) {
			throw new InvalidConditionException(e.getClass() + ": " + e.getMessage());
		}

		_operator.initId();

		mechanism.getPolicyDecisionPoint().addObservers(_operator.getObservers());

		_logger.debug("Condition: {}", _operator);
	}

	@Override
	public String toString() {
		return "Condition: { " + _operator + " }";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		// Local evaluation of the condition's operator
		_logger.debug("Ticking.");
		boolean res = _operator.tick(endOfTimestep);
		_logger.debug("Local evaluation of condition: {}", res);

		if (Settings.getInstance().getDistributionEnabled()) {
			res = _operator.distributedTickPostprocessing(endOfTimestep);
			_logger.debug("Distributed evaluation of condition: {}", res);
		}

		return res;
	}

	@Override
	public void startSimulation() {
		_operator.startSimulation();
	}

	@Override
	public void stopSimulation() {
		_operator.stopSimulation();
	}

	@Override
	public boolean isSimulating() {
		return _operator.isSimulating();
	}
}
