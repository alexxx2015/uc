package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.WithinType;

public class Within extends WithinType {
	private static Logger _logger = LoggerFactory.getLogger(Within.class);
	private TimeAmount _timeAmount = null;

	private Operator op;

	private long _maxCounterValue;

	public Within() {
		/*
		 * The Within Operator evaluates to true,
		 * if this counter has a value larger than 0.
		 */
		_state.set(StateVariable.COUNTER, 0);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op = (Operator) getOperators();

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive.");
		}

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		if (_timeAmount.getTimestepInterval() <= 0) {
			throw new IllegalStateException("Arguments must result in a positive timestepValue.");
		}

		_maxCounterValue = _timeAmount.getTimestepInterval() + 1;

		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "WITHIN(" + _timeAmount + "," + op + " )";
	}

	@Override
	public boolean tick() {
		long counter = _state.get(StateVariable.COUNTER);

		_logger.trace("Current state counter: {}", counter);

		if (op.tick()) {
			/*
			 * Subformula evaluated to true.
			 * Set the counter to its maximum value.
			 */
			counter = _maxCounterValue;
			_logger.debug("Subformula evaluated to true. Resetting counter to {}.", _maxCounterValue);
		} else {
			/*
			 * Subformula evaluated to false.
			 * Decrement the counter, if still greater than 0.
			 */
			if (counter > 0) {
				counter--;
			}
			_logger.debug("Subformula evaluated to false. Decrementing counter to {}.", counter);
		}

		_state.set(StateVariable.COUNTER, counter);

		// The result is true, if the counter is greater than 0.
		// Although we do not need the valueAtLastTick locally,
		// we store it for the superclasses' getValueAtLastTick().
		_state.set(StateVariable.VALUE_AT_LAST_TICK, (counter > 0));
		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
	}
}
