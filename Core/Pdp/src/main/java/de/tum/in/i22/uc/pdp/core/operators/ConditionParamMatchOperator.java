package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType {
	private static Logger _logger = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

	public ConditionParamMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(getClass())
				.add("name", name)
				.add("value", value)
				.add("cmpOp", cmpOp)
				.toString();
	}

//	@Override
//	protected boolean localEvaluation(IEvent curEvent) {
//		_logger.debug("ConditionParamMatchOperator");
//
//		if (curEvent == null) {
//			_logger.debug("null event received. ConditionParamMatchOperator returns false.");
//			return false;
//		}
//
//		ParamMatch pm = new ParamMatch(this.getName(), this.getValue(),this.getCmpOp(), _pdp);
//		return pm.matches(pm.getName(), curEvent.getParameterValue(pm.getName()));
//	}
//
//	protected boolean matches(IEvent ev) {
//		_logger.debug("ConditionParamMatchOperator");
//
//		ParamMatch pm = new ParamMatch(this.getName(), this.getValue(),this.getCmpOp(), _pdp);
//		return pm.matches(pm.getName(), ev.getParameterValue(pm.getName()));
//	}

	@Override
	public boolean tick() {
		return false;
	}
}
