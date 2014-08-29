package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.ParamMatch;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

	public ConditionParamMatchOperator() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(getClass())
				.add("name", name)
				.add("value", value)
				.add("cmpOp", cmpOp)
				.toString();
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.debug("ConditionParamMatchOperator");

		if (curEvent == null) {
			_logger.debug("null event received. ConditionParamMatchOperator returns false.");
			return false;
		}

		ParamMatch pm = new ParamMatch(this.getName(), this.getValue(),this.getCmpOp(), _pdp);
		return pm.matches(pm.getName(), curEvent.getParameterValue(pm.getName()));
	}
}
