package de.tum.in.i22.pdp.datatypes.basic;

import java.util.Map;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IStateEventMap;

public class StateEventMapBasic 
	implements IStateEventMap {
	
	private Map<String, IEvent> _map;

	public StateEventMapBasic(Map<String, IEvent> map) {
		super();
		_map = map;
	}
	
	@Override
	public Map<String, IEvent> getMap() {
		return _map;
	}

}
