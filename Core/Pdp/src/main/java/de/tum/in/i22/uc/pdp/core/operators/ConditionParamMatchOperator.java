package de.tum.in.i22.uc.pdp.core.operators;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType {

	public ConditionParamMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("name", name)
				.add("value", value)
				.add("cmpOp", cmpOp)
				.toString();
	}

	@Override
	public boolean tick() {
		return false;
	}
}
