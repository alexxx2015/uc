package de.tum.in.i22.uc.pdp.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.operators.Operator;
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

		Collection<Observer> observers = new LinkedList<>();
		for (Observer o : _operator.getObservers(observers)) {
			mechanism.getPolicyDecisionPoint().addObserver(o);
		}



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
}
