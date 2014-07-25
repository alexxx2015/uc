package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger log = LoggerFactory.getLogger(Always.class);

	public Always() {
	}

	public Always(Operator operand1) {
		this.setOperators(operand1);
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators()).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "ALWAYS (" + this.getOperators() + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		if (!this._state.immutable) {
			this._state.value = ((Operator) this.getOperators()).evaluate(curEvent);
			if (!this._state.value && curEvent == null) {
				log.debug("evaluating ALWAYS: activating IMMUTABILITY");
				this._state.immutable = true;
			}
		}
		log.debug("eval ALWAYS [{}]", this._state.value);
		return this._state.value;
	}
}
