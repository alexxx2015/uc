package de.tum.in.i22.pdp.datatypes.basic;

import de.tum.in.i22.pdp.datatypes.ICondition;
import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.datatypes.ISimplifiedTemporalLogic;

public class ConditionBasic implements ICondition {
	private IOslFormula _condition;
	private ISimplifiedTemporalLogic _conditionSimp;

	public ConditionBasic(IOslFormula condition,
			ISimplifiedTemporalLogic conditionSimp) {
		super();
		_condition = condition;
		_conditionSimp = conditionSimp;
	}

	@Override
	public IOslFormula getCondition() {
		return _condition;
	}

	@Override
	public ISimplifiedTemporalLogic getConditionSimp() {
		return _conditionSimp;
	}

}
