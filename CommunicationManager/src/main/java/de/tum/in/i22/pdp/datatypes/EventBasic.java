package de.tum.in.i22.pdp.datatypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.pdp.gpb.PdpProtos;
import de.tum.in.i22.pdp.gpb.PdpProtos.Event;
import de.tum.in.i22.pdp.gpb.PdpProtos.Event.MapEntry;

public class EventBasic implements IEvent {
	private String _name = null;
	private Map<String, String> _map = null;

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		_map = map;
	}
	
	public EventBasic(Event event) {
		_name = event.getName();
		
		//number of elements in the map
		int count = event.getMapEntryCount();
		_map = new HashMap<String, String>();
		if (count > 0) {
			Iterator<MapEntry> it = event.getMapEntryList().iterator();
			while (it.hasNext()) {
				MapEntry entry = it.next();
				_map.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public String getName() {
		return _name;
	}

	public Map<String, String> getParameters() {
		return _map;
	}
	
	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer equivalent to IEvent
	 */
	public static Event createGpbEvent(IEvent e) {
		PdpProtos.Event.Builder event = PdpProtos.Event.newBuilder();
		event.setName(e.getName());
		Map<String, String> map = e.getParameters();
		
		if (map != null && !map.isEmpty()) {
			Set<String> keys = map.keySet();
			for (String key:keys) {
				String value = map.get(key);
				PdpProtos.Event.MapEntry.Builder entry = PdpProtos.Event.MapEntry.newBuilder();
				entry.setKey(key);
				entry.setValue(value);
				
				event.addMapEntry(entry);
			}
		}
		
		return event.build();
	}

}
