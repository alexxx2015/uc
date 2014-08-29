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

	public During() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		timeAmount = new TimeAmount(getAmount(), getUnit(), mech.getTimestepSize());

		op = (Operator) operators;

		// for evaluation without history set counter to interval for DURING
		_state.setCounter(timeAmount.getTimestepInterval() + 1);

		op.init(mech);
	}

	@Override
	int initId(int id) {
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}


	@Override
	public String toString() {
		return "DURING (" + timeAmount + ", " + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.trace("current state counter: {}", _state.getCounter());
		if (_state.getCounter() == 0)
			_state.setValue(true);
		else
			_state.setValue(false);

		if (curEvent == null) {
			boolean operandValue = op.evaluate(curEvent);
			if (!operandValue) {
				_state.setCounter(timeAmount.getTimestepInterval() + 1);
				_logger.debug("[DURING] Set negative counter to interval=[{}] due to subformulas state value=[{}]",
						_state.getCounter(), operandValue);
			} else {
				if (_state.getCounter() > 0)
					_state.decCounter();
				_logger.debug("[DURING} New state counter: [{}]", _state.getCounter());
			}

			// update state->value for logging output
			if (_state.getCounter() == 0)
				_state.setValue(true);
			else
				_state.setValue(false);
		}

		_logger.debug("eval DURING [{}]", _state.value());
		return _state.value();
	}
}
