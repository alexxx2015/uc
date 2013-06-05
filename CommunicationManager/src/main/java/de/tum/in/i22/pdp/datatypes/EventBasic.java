package de.tum.in.i22.pdp.datatypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.pdp.gpb.PdpProtos;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent.MapEntry;

public class EventBasic implements IEvent {
	private String _name = null;
	private Map<String, String> _map = null;
	private long _timestamp;

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		_map = map;
	}
	
	public EventBasic(GpEvent event) {
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
	
	@Override
	public long getTimestamp() {
		return _timestamp;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, String> getParameters() {
		return _map;
	}
	
	public void setTimestamp(long timestamp) {
		_timestamp = timestamp;
	}
	
	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer equivalent to IEvent
	 */
	public static GpEvent createGpbEvent(IEvent e) {
		PdpProtos.GpEvent.Builder gpEvent = PdpProtos.GpEvent.newBuilder();
		gpEvent.setName(e.getName());
		Map<String, String> map = e.getParameters();
		
		if (map != null && !map.isEmpty()) {
			Set<String> keys = map.keySet();
			for (String key:keys) {
				String value = map.get(key);
				PdpProtos.GpEvent.MapEntry.Builder entry = PdpProtos.GpEvent.MapEntry.newBuilder();
				entry.setKey(key);
				entry.setValue(value);
				
				gpEvent.addMapEntry(entry);
			}
		}
		
		return gpEvent.build();
	}

	@Override
	public String toString() {
		return "EventBasic [_name=" + _name + "]";
	}

}
