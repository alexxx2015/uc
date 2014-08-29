package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.WithinType;

public class Within extends WithinType {
	private static Logger _logger = LoggerFactory.getLogger(Within.class);
	private TimeAmount timeAmount = null;

	private Operator op;

	public Within() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op = (Operator) this.getOperators();

		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
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
		return "WITHIN (" + this.timeAmount + ", " + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.debug("[WITHIN] Current state counter=[{}]", this._state.getCounter());
		if (this._state.getCounter() > 0)
			this._state.setValue(true);
		else
			this._state.setValue(false);

		if (curEvent == null) {
			boolean operandValue = op.evaluate(curEvent);
			if (operandValue) {
				this._state.setCounter(this.timeAmount.getTimestepInterval() + 1);
				_logger.debug("[WITHIN] Set negative counter to interval=[{}] due to subformulas state value=[{}]",
						this._state.getCounter(), operandValue);
			} else {
				if (this._state.getCounter() > 0)
					this._state.decCounter();
				_logger.debug("[WITHIN} New state counter: [{}]", this._state.getCounter());
			}

			// update state->value for logging output
			if (this._state.getCounter() > 0)
				this._state.setValue(true);
			else
				this._state.setValue(false);
		}

		_logger.debug("eval WITHIN [{}]", this._state.value());
		return this._state.value();
	}

}
