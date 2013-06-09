package de.tum.in.i22.pdp.datatypes.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.ICondition;
import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpCondition;

public class ConditionBasic implements ICondition {
	private static Logger _logger = Logger.getLogger(ConditionBasic.class);
	private IOslFormula _condition;
	private ISimplifiedTemporalLogic _conditionSimp;

	public ConditionBasic(GpCondition gpCondition) {
		if (gpCondition == null)
			return;
		_condition = new OslFormulaBasic(gpCondition.getCondition());
		
		_conditionSimp = 
				new SimplifiedTemporalLogicBasic(
						gpCondition.getConditionSimp());
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
	
	/**
	 * 
	 * @return Google Protocol Buffer object corresponding to ICondition
	 */
	public static GpCondition createGpbCondition(ICondition condition) {
		if (condition == null) 
			return null;
		_logger.trace("Build condition");
		
		GpCondition.Builder gp = GpCondition.newBuilder();
		gp.setCondition(OslFormulaBasic.createGpbOslFormula(condition.getCondition()));
		gp.setConditionSimp(
				SimplifiedTemporalLogicBasic.createGpbSimplifiedTemporalLogic(
						condition.getConditionSimp()));
		
		return gp.build();
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			ConditionBasic o = (ConditionBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_condition, o.getCondition())
					&& CompareUtil.areObjectsEqual(_conditionSimp, o.getConditionSimp());
		}
		return isEqual;
	}
}
