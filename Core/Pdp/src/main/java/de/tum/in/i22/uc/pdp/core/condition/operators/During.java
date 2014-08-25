package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.DuringType;

public class During extends DuringType {
	private static Logger _logger = LoggerFactory.getLogger(During.class);
	private TimeAmount timeAmount = null;

	public During() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());

		// for evaluation without history set counter to interval for DURING
		this._state.counter = this.timeAmount.getTimestepInterval() + 1;

		((Operator) this.getOperators()).init(mech);
	}

	@Override
	public String toString() {
		return "DURING (" + this.timeAmount + ", " + this.getOperators() + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_logger.trace("current state counter: {}", this._state.counter);
		if (this._state.counter == 0)
			this._state.value = true;
		else
			this._state.value = false;

		if (curEvent == null) {
			boolean operandValue = ((Operator) this.getOperators()).evaluate(curEvent);
			if (!operandValue) {
				this._state.counter = this.timeAmount.getTimestepInterval() + 1;
				_logger.debug("[DURING] Set negative counter to interval=[{}] due to subformulas state value=[{}]",
						this._state.counter, operandValue);
			} else {
				if (this._state.counter > 0)
					this._state.counter--;
				_logger.debug("[DURING} New state counter: [{}]", this._state.counter);
			}

			// update state->value for logging output
			if (this._state.counter == 0)
				this._state.value = true;
			else
				this._state.value = false;
		}

		_logger.debug("eval DURING [{}]", this._state.value);
		return this._state.value;
	}
}
