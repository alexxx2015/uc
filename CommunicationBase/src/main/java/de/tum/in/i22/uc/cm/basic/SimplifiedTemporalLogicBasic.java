package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.IDataEventMap;
import de.tum.in.i22.uc.cm.datatypes.IOslFormula;
import de.tum.in.i22.uc.cm.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.uc.cm.datatypes.IStateEventMap;

public class SimplifiedTemporalLogicBasic
	implements ISimplifiedTemporalLogic {

	private static Logger _logger = Logger.getLogger(SimplifiedTemporalLogicBasic.class);
	private IDataEventMap _dataEventMap;
	private IOslFormula _formula;
	private IStateEventMap _stateEventMap;

	public SimplifiedTemporalLogicBasic() {
	}

	public SimplifiedTemporalLogicBasic(IDataEventMap dataEventMap,
			IOslFormula formula, IStateEventMap stateEventMap) {
		super();
		_dataEventMap = dataEventMap;
		_formula = formula;
		_stateEventMap = stateEventMap;
	}


	@Override
	public IDataEventMap getDataEventMap() {
		return _dataEventMap;
	}

	@Override
	public IOslFormula getFormula() {
		return _formula;
	}

	@Override
	public IStateEventMap getStateEventMap() {
		return _stateEventMap;
	}


	public void setDataEventMap(IDataEventMap dataEventMap) {
		_dataEventMap = dataEventMap;
	}

	public void setFormula(IOslFormula formula) {
		_formula = formula;
	}

	public void setStateEventMap(IStateEventMap stateEventMap) {
		_stateEventMap = stateEventMap;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof SimplifiedTemporalLogicBasic) {
			SimplifiedTemporalLogicBasic o = (SimplifiedTemporalLogicBasic)obj;
			isEqual = Objects.equals(_dataEventMap, o._dataEventMap)
					&& Objects.equals(_formula, o._formula)
					&& Objects.equals(_stateEventMap, o._stateEventMap);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_dataEventMap, _formula, _stateEventMap);
	}

}
