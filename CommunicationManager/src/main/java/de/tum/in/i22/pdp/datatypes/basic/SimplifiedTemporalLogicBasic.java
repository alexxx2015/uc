package de.tum.in.i22.pdp.datatypes.basic;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IDataEventMap;
import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.pdp.datatypes.IStateEventMap;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpSimplifiedTemporalLogic;

public class SimplifiedTemporalLogicBasic 
	implements ISimplifiedTemporalLogic {
	
	private static Logger _logger = Logger.getLogger(SimplifiedTemporalLogicBasic.class);
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

	public SimplifiedTemporalLogicBasic(GpSimplifiedTemporalLogic gp) {
		if (gp == null)
			return;
		_dataEventMap = new DataEventMapBasic(gp.getDataEventMap());
		_formula = new OslFormulaBasic(gp.getFormula());
		_stateEventMap = new StateEventMapBasic(gp.getStateEventMap());
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
	
	/**
	 * 
	 * @return Google Protocol Buffer object corresponding to ISimplifiedTemporalLogic
	 */
	public static GpSimplifiedTemporalLogic createGpbSimplifiedTemporalLogic(
			ISimplifiedTemporalLogic obj) {
		if (obj == null)
			return null;
		_logger.trace("Build GpSimplifiedTemporalLogic");
		GpSimplifiedTemporalLogic.Builder gp = GpSimplifiedTemporalLogic.newBuilder();
		gp.setDataEventMap(
				DataEventMapBasic.createGpbDataEventMap(
						obj.getDataEventMap()));
		gp.setFormula(
				OslFormulaBasic.createGpbOslFormula(
						obj.getFormula()));
		gp.setStateEventMap(
				StateEventMapBasic.createGpbStateEventMap(
						obj.getStateEventMap()));
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			SimplifiedTemporalLogicBasic o = (SimplifiedTemporalLogicBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_dataEventMap, o.getDataEventMap()) 
					&& CompareUtil.areObjectsEqual(_formula, o.getFormula())
					&& CompareUtil.areObjectsEqual(_stateEventMap, o.getStateEventMap());
		}
		return isEqual;
	}

}
