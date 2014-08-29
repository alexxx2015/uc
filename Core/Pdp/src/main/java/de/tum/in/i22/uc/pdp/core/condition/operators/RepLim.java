package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
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
		this._state.newCircArray(this.timeAmount.getTimestepInterval());
		for (int a = 0; a < this.timeAmount.getTimestepInterval(); a++)
			this._state.getCircArray().set(false, a);

		op = (Operator) operators;
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
		return "REPLIM (" + this.timeAmount + ", " + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.debug("circularArray: {}", this._state.getCircArray());
		if (this._state.getCounter() >= this.getLowerLimit() && this._state.getCounter() <= this.getUpperLimit())
			this._state.setValue(true);
		else
			this._state.setValue(false);

		if (curEvent == null) {
			Boolean curValue = this._state.getCircArray().pop();
			if (curValue) {
				this._state.decCounter();
				_logger.debug("[REPLIM] Decrementing counter to [{}]", this._state.getCounter());
			}

			Boolean operandState = op.evaluate(curEvent);
			if (operandState) {
				this._state.incCounter();
				_logger.debug("[REPLIM] Incrementing counter to [{}] due to intercepted event", this._state.getCounter());
			}

			this._state.getCircArray().push(operandState);
			_logger.debug("circularArray: {}", this._state.getCircArray());
		}

		_logger.debug("eval REPLIM [{}]", this._state.value());
		return this._state.value();
	}
}
