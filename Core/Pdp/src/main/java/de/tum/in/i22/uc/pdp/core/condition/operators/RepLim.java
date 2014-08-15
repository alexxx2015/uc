package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.CircularArray;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger log = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount timeAmount = null;

	public RepLim() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
		this._state.circArray = new CircularArray<Boolean>(this.timeAmount.getTimestepInterval());
		for (int a = 0; a < this.timeAmount.getTimestepInterval(); a++)
			this._state.circArray.set(false, a);
		((Operator) this.getOperators()).init(mech);
	}

	@Override
	public String toString() {
		return "REPLIM (" + this.timeAmount + ", " + this.getOperators() + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		log.debug("circularArray: {}", this._state.circArray);
		if (this._state.counter >= this.getLowerLimit() && this._state.counter <= this.getUpperLimit())
			this._state.value = true;
		else
			this._state.value = false;

		if (curEvent == null) {
			Boolean curValue = this._state.circArray.pop();
			if (curValue) {
				this._state.counter--;
				log.debug("[REPLIM] Decrementing counter to [{}]", this._state.counter);
			}

			Boolean operandState = ((Operator) this.getOperators()).evaluate(curEvent);
			if (operandState) {
				this._state.counter++;
				log.debug("[REPLIM] Incrementing counter to [{}] due to intercepted event", this._state.counter);
			}

			this._state.circArray.push(operandState);
			log.debug("circularArray: {}", this._state.circArray);
		}

		log.debug("eval REPLIM [{}]", this._state.value);
		return this._state.value;
	}
}
