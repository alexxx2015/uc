package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.CircularArray;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	public TimeAmount _timeAmount = null;

	public Before() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		this._state.circArray = new CircularArray<Boolean>(this._timeAmount.timestepInterval);
		for (int a = 0; a < _timeAmount.timestepInterval; a++) {
			_state.circArray.set(false, a);
		}
		((Operator) operators).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "BEFORE (" + this._timeAmount + ", " + this.getOperators() + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) { // before = at (currentTime -
												// interval) operand was true
		_logger.debug("circularArray: {}", this._state.circArray);

		Boolean curValue = this._state.circArray.readFirst();
		this._state.value = curValue;
		if (curEvent == null) {
			curValue = this._state.circArray.pop();
			Boolean operandState = ((Operator) this.getOperators()).evaluate(curEvent);
			this._state.circArray.push(operandState);

			_logger.debug("circularArray: {}", this._state.circArray);
		}

		_logger.debug("eval BEFORE [{}]", this._state.value);
		return this._state.value;
	}
}
