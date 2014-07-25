package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.ParamMatch;
import de.tum.in.i22.uc.pdp.xsd.ConditionParamMatchType;

public class ConditionParamMatchOperator extends ConditionParamMatchType {
	private static Logger log = LoggerFactory.getLogger(ConditionParamMatchOperator.class);

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
		log.debug("ConditionParamMatchOperator");

		if (curEvent == null) {
			log.debug("null event received. ConditionParamMatchOperator returns false.");
			return false;
		}

		// creates a corresponding paramMatch object

		ParamMatch pm = new ParamMatch();
		pm.setCmpOp(this.getCmpOp());
		pm.setName(this.getName());
		pm.setValue(this.getValue());

		pm.setPdp(_pdp);
		// use the parmMatches method for the evaluation

		return pm.paramMatches(curEvent.getParameter(pm.getName()));
	}
}
