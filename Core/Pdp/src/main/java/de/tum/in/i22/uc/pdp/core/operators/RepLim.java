package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger _logger = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount timeAmount = null;
	private Operator op;

	private long _stateCounter = 0;
	private CircularArray<Boolean> _stateCircArray;

	private long _backupStateCounter;
	private CircularArray<Boolean> _backupStateCircArray;

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
		op.init(mech, this, Math.max(ttl, timeAmount.getInterval() + 2 * mech.getTimestepSize()));
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "REPLIM(" + timeAmount + ", " + op + " )";
	}

//	@Override
//	protected boolean localEvaluation(IEvent ev) {
//		_logger.debug("circularArray: {}", _stateCircArray);
//
//		boolean result = (_stateCounter >= lowerLimit && _stateCounter <= upperLimit);
//
//		if (ev == null) {
//			/*
//			 * We are evaluating at the end of a timestep
//			 */
//
//			if (_stateCircArray.pop()) {
//				_stateCounter--;
//				_logger.debug("Oldest value was true. Removing it. Therefore also decrementing counter to {}.", _stateCounter);
//			}
//
//			boolean operandState = op.evaluate(null);
//			if (operandState) {
//				_stateCounter++;
//				_logger.debug("Current value is true. Therefore incrementing counter to {}.", _stateCounter);
//			}
//
//			_stateCircArray.push(operandState);
//			_logger.debug("circularArray: {}", _stateCircArray);
//		}
//
//		return result;
//	}

	@Override
	public boolean tick() {
		_logger.debug("circularArray: {}", _stateCircArray);

		_valueAtLastTick = (_stateCounter >= lowerLimit && _stateCounter <= upperLimit);

		if (_stateCircArray.pop()) {
			_stateCounter--;
			_logger.debug("Oldest value was true. Removing it. Therefore also decrementing counter to {}.", _stateCounter);
		}

		boolean operandState = op.tick();
		if (operandState) {
			_stateCounter++;
			_logger.debug("Current value is true. Therefore incrementing counter to {}.", _stateCounter);
		}

		_stateCircArray.push(operandState);
		_logger.debug("circularArray: {}", _stateCircArray);

		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
		_backupStateCircArray = _stateCircArray.clone();
		_backupStateCounter = _stateCounter;
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
		_stateCircArray = _backupStateCircArray;
		_stateCounter = _backupStateCounter;
	}
}
