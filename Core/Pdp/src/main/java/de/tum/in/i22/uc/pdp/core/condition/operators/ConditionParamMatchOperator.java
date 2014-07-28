package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.ParamMatch;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType {
	private static Logger _logger = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

	public ConditionParamMatchOperator() {
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return "ConditionParamMatchOperator [Name: " + this.getName() + ", Value: " + this.getValue() + ", CompOp: "
				+ this.getCmpOp() + "]";

	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		_logger.debug("ConditionParamMatchOperator");

		if (curEvent == null) {
			_logger.debug("null event received. ConditionParamMatchOperator returns false.");
			return false;
		}

		ParamMatch pm = new ParamMatch(this.getName(), this.getValue(),this.getCmpOp(), _pdp);
		return pm.matches(pm.getName(), curEvent.getParameterValue(pm.getName()));
	}
}
