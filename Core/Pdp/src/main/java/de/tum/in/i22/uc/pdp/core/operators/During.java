package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.xsd.DuringType;

public class During extends DuringType {
	private static Logger _logger = LoggerFactory.getLogger(During.class);
	private TimeAmount timeAmount = null;

	private Operator op;

	private long _initialCounterValue;

	private long _stateCounter;

	private final Deque<Long> _backupStateCounter;

	public During() {
		_backupStateCounter = new ArrayDeque<>(2);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		timeAmount = new TimeAmount(getAmount(), getUnit(), mech.getTimestepSize());

		op = (Operator) operators;

		_initialCounterValue = timeAmount.getTimestepInterval() + 1;

		/*
		 * The During Operator evaluates to true,
		 * if this counter reaches a value of 0.
		 */
		_stateCounter = _initialCounterValue;

		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}


	@Override
	public String toString() {
		return "DURING(" + timeAmount + "," + op + " )";
	}

	@Override
	public boolean tick() {
		_logger.trace("Current state counter: {}", _stateCounter);

		if (op.tick()) {
			/*
			 * Subformula evaluated to true.
			 * Decrement the counter if it is still positive.
			 */
			if (_stateCounter > 0) {
				_stateCounter--;
			}
			_logger.debug("Subformula evaluated to true. Decrementing counter to {}.", _stateCounter);
		}
		else {
			/*
			 * Subformula evaluated to false.
			 * Reset the counter to the initial value.
			 */
			_stateCounter = _initialCounterValue;
			_logger.debug("Subformula evaluated to false. Resetting counter to {}.", _initialCounterValue);

		}

		// The result is true, if the counter reaches 0.
		_valueAtLastTick = (_stateCounter == 0);
		_logger.debug("Result: {}.", _valueAtLastTick);
		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
		_backupStateCounter.addFirst(_stateCounter);
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
		_stateCounter = _backupStateCounter.getFirst();
	}
}
