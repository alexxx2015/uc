package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.ICondition;
import de.tum.in.i22.uc.cm.datatypes.IOslFormula;
import de.tum.in.i22.uc.cm.datatypes.ISimplifiedTemporalLogic;

public class ConditionBasic implements ICondition {
	private IOslFormula _condition;
	private ISimplifiedTemporalLogic _conditionSimp;

	public ConditionBasic() {
	}

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

	public void setCondition(IOslFormula condition) {
		_condition = condition;
	}

	public void setConditionSimp(ISimplifiedTemporalLogic conditionSimp) {
		_conditionSimp = conditionSimp;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof ConditionBasic) {
			ConditionBasic o = (ConditionBasic)obj;
			isEqual = Objects.equals(_condition, o._condition)
					&& Objects.equals(_conditionSimp, o._conditionSimp);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_condition, _conditionSimp);
	}
}
