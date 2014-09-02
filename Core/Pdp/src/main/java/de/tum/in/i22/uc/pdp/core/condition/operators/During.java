package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.DuringType;

public class During extends DuringType {
	private static Logger _logger = LoggerFactory.getLogger(During.class);
	private TimeAmount timeAmount = null;

	private Operator op;

	private long _initialCounterValue;

	private long _stateCounter;

	public During() {
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
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}


	@Override
	public String toString() {
		return "DURING(" + timeAmount + "," + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		_logger.trace("Current state counter: {}", _stateCounter);

		if (ev == null) {
			/*
			 * We are updating at the end of a timestep
			 */

			if (op.evaluate(null)) {
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
		}

		// The result is true, if the counter reaches 0.
		return (_stateCounter == 0);
	}
}
