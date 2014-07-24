package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.xsd.DuringType;

public class During extends DuringType {
	private static Logger log = LoggerFactory.getLogger(During.class);
	public TimeAmount timeAmount = null;

	public During() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());

		// for evaluation without history set counter to interval for DURING
		this._state.counter = this.timeAmount.timestepInterval + 1;

		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "DURING (" + this.timeAmount + ", " + this.getOperators() + " )";
	}

	@Override
	public boolean evaluate(Event curEvent) {
		log.trace("current state counter: {}", this._state.counter);
		if (this._state.counter == 0)
			this._state.value = true;
		else
			this._state.value = false;

		if (curEvent == null) {
			boolean operandValue = ((Operator) this.getOperators()).evaluate(curEvent);
			if (!operandValue) {
				this._state.counter = this.timeAmount.timestepInterval + 1;
				log.debug("[DURING] Set negative counter to interval=[{}] due to subformulas state value=[{}]",
						this._state.counter, operandValue);
			} else {
				if (this._state.counter > 0)
					this._state.counter--;
				log.debug("[DURING} New state counter: [{}]", this._state.counter);
			}

			// update state->value for logging output
			if (this._state.counter == 0)
				this._state.value = true;
			else
				this._state.value = false;
		}

		log.debug("eval DURING [{}]", this._state.value);
		return this._state.value;
	}
}
