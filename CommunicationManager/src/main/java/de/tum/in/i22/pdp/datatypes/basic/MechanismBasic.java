package de.tum.in.i22.pdp.datatypes.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.pdp.datatypes.ICondition;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IDataEventMap;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IHistory;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IOslFormula;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.ISimplifiedTemporalLogic;
import de.tum.in.i22.pdp.datatypes.IStateEventMap;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpCondition;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpData;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataEventMap;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataEventMap.GpDataEventMapEntry;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpHistory;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpOslFormula;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpSimplifiedTemporalLogic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStateEventMap;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStateEventMap.GpStateEventMapEntry;

public class MechanismBasic implements IMechanism {
	private ICondition _condition;
	private String _mechanismName;
	private IResponse _response;
	private IHistory _state;
	private IEvent _triggerEvent;
	
	public MechanismBasic(GpMechanism gpM) {
		_mechanismName = gpM.getMechanismName();
		
		_condition = populateCondition(gpM.getCondition());
		
		if (gpM.getResponse() != null) {
			_response = new ResponseBasic(gpM.getResponse());
		}
		
		_state = populateState(gpM.getState());
		
		if (gpM.getTriggerEvent() != null) {
			_triggerEvent = new EventBasic(gpM.getTriggerEvent());
		}
	}

	@Override
	public String getMechanismName() {
		return _mechanismName;
	}

	@Override
	public ICondition getCondition() {
		return _condition;
	}

	@Override
	public IResponse getResponse() {
		return _response;
	}

	@Override
	public IHistory getState() {
		return _state;
	}

	@Override
	public IEvent getTriggerEvent() {
		return _triggerEvent;
	}
	
	private IHistory populateState(GpHistory state) {
		if (state == null) {
			return null;
		}
		
		List<GpEvent> list = state.getTraceList();
		if (list != null && !list.isEmpty()) {
			List<IEvent> actualList = new ArrayList<IEvent>();
			for (GpEvent event:list) {
				actualList.add(new EventBasic(event));
			}
			
			return new HistoryBasic(actualList);
		} else {
			return null;
		}
	}
	
	private ICondition populateCondition(GpCondition gpCondition) {
		if (gpCondition == null) {
			return null;
		}
		
		GpOslFormula gpConditionAttribute = gpCondition.getCondition();
		IOslFormula condition = populateCondition(gpConditionAttribute);
		
		GpSimplifiedTemporalLogic gpConditionSimp = gpCondition.getConditionSimp(); 
		ISimplifiedTemporalLogic conditionSimp = populateConditionSimp(gpConditionSimp);
		
		return (new ConditionBasic(condition, conditionSimp));
		
	}

	private IOslFormula populateCondition(GpOslFormula gpConditionAttribute) {
		if (gpConditionAttribute == null) {
			return null;
		}
		
		return new OslFormulaBasic(gpConditionAttribute.getFormula());
	}
	
	private ISimplifiedTemporalLogic populateConditionSimp(
			GpSimplifiedTemporalLogic gpConditionSimp) {
		
		if (gpConditionSimp == null) {
			return null;
		}
		
		IDataEventMap dataEventMap = populateDataEventMap(gpConditionSimp.getDataEventMap());
		IOslFormula formula = populateOslFormula(gpConditionSimp.getFormula());
		IStateEventMap stateEventMap = populateStateEventMap(gpConditionSimp.getStateEventMap());
		
		SimplifiedTemporalLogicBasic simpTemporalLogic =
				new SimplifiedTemporalLogicBasic(
						dataEventMap,
						formula,
						stateEventMap);
		
		return simpTemporalLogic;
	}


	private IStateEventMap populateStateEventMap(GpStateEventMap stateEventMap) {
		if (stateEventMap == null) {
			return null;
		}
		
		List<GpStateEventMapEntry> list = stateEventMap.getMapEntryList();
		if (list != null && !list.isEmpty()) {
			Map<String, IEvent> map = new HashMap<>();
			
			for (GpStateEventMapEntry entry:list) {
				String key = entry.getKey();
				IEvent event = new EventBasic(entry.getValue());
				map.put(key, event);
			}
			
			StateEventMapBasic stateEventMapBasic =
					new StateEventMapBasic(map);
			return stateEventMapBasic;
		} else {
			return null;
		}
	}

	private IOslFormula populateOslFormula(GpOslFormula formula) {
		if (formula == null) {
			return null;
		}
		
		return new OslFormulaBasic(formula.getFormula());
	}

	private IDataEventMap populateDataEventMap(GpDataEventMap gpDataEventMap) {
		if (gpDataEventMap == null) {
			return null;
		}
		
		
		List<GpDataEventMapEntry> list = gpDataEventMap.getMapEntryList();
		if (list != null && !list.isEmpty()) {
			Map<IData, IEvent> map = new HashMap<>();
			
			for (GpDataEventMapEntry entry:list) {
				GpData gpData = entry.getKey();
				GpEvent gpEvent = entry.getValue();
				
				map.put(new DataBasic(gpData.getId()), new EventBasic(gpEvent));
			}
			
			DataEventMapBasic dataEventMap = new DataEventMapBasic(map);
			
			return dataEventMap;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer object corresponding to IMechanism
	 */
	public static GpMechanism createGpbMechanism(IMechanism m) {
		//TODO implement
		return null;
	}

	
}
