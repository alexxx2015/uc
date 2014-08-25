package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.CircularArray;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger _logger = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount timeAmount = null;
	private Operator op;

	public RepLim() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
		this._state.circArray = new CircularArray<Boolean>(this.timeAmount.getTimestepInterval());
		for (int a = 0; a < this.timeAmount.getTimestepInterval(); a++)
			this._state.circArray.set(false, a);

		op = (Operator) operators;
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
		return "REPLIM (" + this.timeAmount + ", " + op + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_logger.debug("circularArray: {}", this._state.circArray);
		if (this._state.counter >= this.getLowerLimit() && this._state.counter <= this.getUpperLimit())
			this._state.value = true;
		else
			this._state.value = false;

		if (curEvent == null) {
			Boolean curValue = this._state.circArray.pop();
			if (curValue) {
				this._state.counter--;
				_logger.debug("[REPLIM] Decrementing counter to [{}]", this._state.counter);
			}

			Boolean operandState = op.evaluate(curEvent);
			if (operandState) {
				this._state.counter++;
				_logger.debug("[REPLIM] Incrementing counter to [{}] due to intercepted event", this._state.counter);
			}

			this._state.circArray.push(operandState);
			_logger.debug("circularArray: {}", this._state.circArray);
		}

		_logger.debug("eval REPLIM [{}]", this._state.value);
		return this._state.value;
	}
}
