package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger _logger = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount timeAmount = null;
	private Operator op;

	private long _stateCounter = 0;
	private CircularArray<Boolean> _stateCircArray;

	public RepLim() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		timeAmount = new TimeAmount(getAmount(), getUnit(), mech.getTimestepSize());

		_stateCircArray = new CircularArray<>(timeAmount.getTimestepInterval());
		for (int a = 0; a < timeAmount.getTimestepInterval(); a++) {
			_stateCircArray.set(false, a);
		}

		op = (Operator) operators;
		op.init(mech, this, Math.max(ttl, timeAmount.getInterval() + mech.getTimestepSize()));
	}

	@Override
	protected int initId(int id) {
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "REPLIM(" + timeAmount + ", " + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		_logger.debug("circularArray: {}", _stateCircArray);

		boolean result = (_stateCounter >= lowerLimit && _stateCounter <= upperLimit);

		if (ev == null) {
			/*
			 * We are evaluating at the end of a timestep
			 */

			if (_stateCircArray.pop()) {
				_stateCounter--;
				_logger.debug("Oldest value was true. Removing it. Therefore also decrementing counter to {}.", _stateCounter);
			}

			boolean operandState = op.evaluate(null);
			if (operandState) {
				_stateCounter++;
				_logger.debug("Current value is true. Therefore incrementing counter to {}.", _stateCounter);
			}

			_stateCircArray.push(operandState);
			_logger.debug("circularArray: {}", _stateCircArray);
		}

		return result;
	}
}
