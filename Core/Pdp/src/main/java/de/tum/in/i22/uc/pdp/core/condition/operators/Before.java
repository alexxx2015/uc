package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.condition.CircularArray;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger log = LoggerFactory.getLogger(Before.class);
	public TimeAmount timeAmount = null;

	public Before() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
		this._state.circArray = new CircularArray<Boolean>(this.timeAmount.timestepInterval);
		for (int a = 0; a < this.timeAmount.timestepInterval; a++)
			this._state.circArray.set(false, a);
		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "BEFORE (" + this.timeAmount + ", " + this.getOperators() + " )";
	}

	@Override
	public boolean evaluate(Event curEvent) { // before = at (currentTime -
												// interval) operand was true
		log.debug("circularArray: {}", this._state.circArray);

		Boolean curValue = this._state.circArray.readFirst();
		this._state.value = curValue;
		if (curEvent == null) {
			curValue = this._state.circArray.pop();
			Boolean operandState = ((Operator) this.getOperators()).evaluate(curEvent);
			this._state.circArray.push(operandState);

			log.debug("circularArray: {}", this._state.circArray);
		}

		log.debug("eval BEFORE [{}]", this._state.value);
		return this._state.value;
	}
}
