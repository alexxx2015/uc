package de.tum.in.i22.pdp.datatypes.basic;

import java.util.Map;

import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IDataEventMap;
import de.tum.in.i22.pdp.datatypes.IEvent;

public class DataEventMapBasic implements IDataEventMap {
	private Map<IData, IEvent> _map;
	
	public DataEventMapBasic(Map<IData, IEvent> map) {
		super();
		_map = map;
	}

	@Override
	public Map<IData, IEvent> getMap() {
		return _map;
	}

}
