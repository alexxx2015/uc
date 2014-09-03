package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.WithinType;

public class Within extends WithinType {
	private static Logger _logger = LoggerFactory.getLogger(Within.class);
	private TimeAmount _timeAmount = null;

	private Operator op;

	private long _maxCounterValue;

	private long _stateCounter;

	public Within() {
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

		/*
		 * The Within Operator evaluates to true,
		 * if this counter has a value larger than 0.
		 */
		_stateCounter = 0;

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
	protected boolean localEvaluation(IEvent ev) {
		_logger.trace("Current state counter: {}", _stateCounter);

		if (ev == null) {
			/*
			 * We are updating at the end of a timestep
			 */

			if (op.evaluate(ev)) {
				/*
				 * Subformula evaluated to true.
				 * Set the counter to its maximum value.
				 */
				_stateCounter = _maxCounterValue;
				_logger.debug("Subformula evaluated to true. Resetting counter to {}.", _maxCounterValue);
			} else {
				/*
				 * Subformula evaluated to false.
				 * Decrement the counter, if still greater than 0.
				 */
				if (_stateCounter > 0) {
					_stateCounter--;
				}
				_logger.debug("Subformula evaluated to false. Decrementing counter to {}.", _stateCounter);
			}
		}

		// The result is true, if the counter is greater than 0
		return (_stateCounter > 0);
	}
}
