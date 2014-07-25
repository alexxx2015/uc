package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepMaxType;

public class RepMax extends RepMaxType {
	private static Logger log = LoggerFactory.getLogger(RepMax.class);

	// public long limit =0;

	public RepMax() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "REPMAX (" + this.getLimit() + ", " + this.getOperators() + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		if (!this._state.immutable) {
			if (curEvent != null && ((Operator) this.getOperators()).evaluate(curEvent)) {
				this._state.counter++;
				log.debug("[REPMAX] Subformula was satisfied; counter incremented to [{}]", this._state.counter);
			}

			if (this._state.counter <= this.getLimit())
				this._state.value = true;
			else
				this._state.value = false;

			if (curEvent == null && !this._state.value) {
				log.debug("[REPMAX] Activating immutability");
				this._state.immutable = true;
			}
		}

		log.debug("eval REPMAX [{}]", this._state.value);
		return this._state.value;
	}
}
