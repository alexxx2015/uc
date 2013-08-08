package de.tum.in.i22.uc.cm.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.ICondition;
import de.tum.in.i22.uc.cm.datatypes.IOslFormula;
import de.tum.in.i22.uc.cm.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCondition;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpOslFormula;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpSimplifiedTemporalLogic;

public class ConditionBasic implements ICondition {
	private static Logger _logger = Logger.getLogger(ConditionBasic.class);
	private IOslFormula _condition;
	private ISimplifiedTemporalLogic _conditionSimp;

	public ConditionBasic() {
	}
	
	public ConditionBasic(GpCondition gpCondition) {
		if (gpCondition == null)
			return;
		
		if (gpCondition.hasCondition())
			_condition = new OslFormulaBasic(gpCondition.getCondition());
		
		if (gpCondition.hasConditionSimp())
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
	
	public void setCondition(IOslFormula condition) {
		_condition = condition;
	}

	public void setConditionSimp(ISimplifiedTemporalLogic conditionSimp) {
		_conditionSimp = conditionSimp;
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
		GpOslFormula gpOslFormula = 
				OslFormulaBasic.createGpbOslFormula(
						condition.getCondition());
		if (gpOslFormula != null)
			gp.setCondition(gpOslFormula);
		
		GpSimplifiedTemporalLogic gpSimplifiedTemporalLogic = 
		SimplifiedTemporalLogicBasic.createGpbSimplifiedTemporalLogic(
				condition.getConditionSimp());
		
		if (gpSimplifiedTemporalLogic != null)
			gp.setConditionSimp(gpSimplifiedTemporalLogic);
		
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
