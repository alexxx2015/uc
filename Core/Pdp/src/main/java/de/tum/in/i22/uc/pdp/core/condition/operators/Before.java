package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.CircularArray;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	private TimeAmount _timeAmount;

	private Operator op;

	public Before() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op = ((Operator) operators);

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		this._state.circArray = new CircularArray<Boolean>(this._timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			_state.circArray.set(false, a);
		}
		op.init(mech);
	}

	@Override
	int initId(int id) {
		_id = op.initId(id) + 1;
		_logger.debug("My [{}] id is {}.", this, _id);
		return _id;
	}

	@Override
	public String toString() {
		return "BEFORE (" + this._timeAmount + ", " + op + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) { // before = at (currentTime -
												// interval) operand was true
		_logger.debug("circularArray: {}", this._state.circArray);

		Boolean curValue = this._state.circArray.readFirst();
		this._state.value = curValue;
		if (curEvent == null) {
			curValue = this._state.circArray.pop();
			Boolean operandState = op.evaluate(curEvent);
			this._state.circArray.push(operandState);

			_logger.debug("circularArray: {}", this._state.circArray);
		}

		_logger.debug("eval BEFORE [{}]", this._state.value);
		return this._state.value;
	}
}
