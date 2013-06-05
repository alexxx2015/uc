package de.tum.in.i22.pdp.datatypes.basic;

import de.tum.in.i22.pdp.datatypes.IDataEventMap;
import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.pdp.datatypes.IStateEventMap;

public class SimplifiedTemporalLogicBasic 
	implements ISimplifiedTemporalLogic {
	
	private IDataEventMap _dataEventMap;
	private IOslFormula _formula;
	private IStateEventMap _stateEventMap;
	
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

}
