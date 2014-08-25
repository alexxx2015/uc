package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.EvalOperatorType;

public class EvalOperator extends EvalOperatorType implements LiteralOperator {

	public EvalOperator() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	public String toString() {
		return "EvalOperator [Type: " + this.getType() + ", [" + this.getContent() + "]]";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		// TODO: evalOperator evaluation NYI; forward to external evaluation engine
		throw new UnsupportedOperationException("evalate EvalOperator. Not yet implemented.");
	}
}
