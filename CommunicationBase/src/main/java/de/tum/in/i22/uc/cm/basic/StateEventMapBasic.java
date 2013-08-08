package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStateEventMap;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStateEventMap;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStateEventMap.GpStateEventMapEntry;

public class StateEventMapBasic 
	implements IStateEventMap {
	
	private static final Logger _logger = Logger
			.getLogger(StateEventMapBasic.class);
	private Map<String, IEvent> _map;

	public StateEventMapBasic(Map<String, IEvent> map) {
		super();
		_map = map;
	}
	
	public StateEventMapBasic(GpStateEventMap gpStateEventMap) {
		if (gpStateEventMap == null) {
			return;
		}
		
		List<GpStateEventMapEntry> list = gpStateEventMap.getMapEntryList();
		if (list != null && !list.isEmpty()) {
			_map = new HashMap<>();
			
			for (GpStateEventMapEntry entry:list) {
				String key = entry.getKey();
				IEvent event = new EventBasic(entry.getValue());
				_map.put(key, event);
			}
		} 
	}

	@Override
	public Map<String, IEvent> getMap() {
		return _map;
	}

	/**
	 * 
	 * @return Google Protocol Buffer object corresponding to IStateEventMap
	 */
	public static GpStateEventMap createGpbStateEventMap(
			IStateEventMap stateEventMap) {
		if (stateEventMap == null)
			return null;
		_logger.trace("Build GpStateEventMap");
		GpStateEventMap.Builder gp = GpStateEventMap.newBuilder();
		Map<String, IEvent> map = stateEventMap.getMap();
		if (map != null && !map.isEmpty()) {
			Set<String> keySet = map.keySet();
			for (String k:keySet) {
				GpStateEventMapEntry.Builder gpe = GpStateEventMapEntry.newBuilder();
				gpe.setKey(k);
				gpe.setValue(EventBasic.createGpbEvent(map.get(k)));
				gp.addMapEntry(gpe);
			}
		}
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			StateEventMapBasic o = (StateEventMapBasic)obj;
			isEqual = CompareUtil.areMapsEqual(_map, o.getMap());
		}
		return isEqual;
	}

}
