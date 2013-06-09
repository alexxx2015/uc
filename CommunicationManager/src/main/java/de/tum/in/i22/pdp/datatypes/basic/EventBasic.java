package de.tum.in.i22.pdp.datatypes.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent.GpMapEntry;

public class EventBasic implements IEvent {
	private String _name = null;
	private Map<String, String> _map = null;
	private long _timestamp;

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		_map = map;
	}
	
	public EventBasic(GpEvent gpEvent) {
		if (gpEvent == null)
			return;
		
		if (gpEvent.hasName())
			_name = gpEvent.getName();
		
		//number of elements in the map
		int count = gpEvent.getMapEntryCount();
		if (count > 0) {
			_map = new HashMap<String, String>();
			Iterator<GpMapEntry> it = gpEvent.getMapEntryList().iterator();
			while (it.hasNext()) {
				GpMapEntry entry = it.next();
				_map.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Insert timestamp
		if (gpEvent.hasTimestamp() && gpEvent.getTimestamp() != null && !gpEvent.getTimestamp().isEmpty())
			_timestamp = Long.valueOf(gpEvent.getTimestamp());
		else
			_timestamp = 0;
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
	 * @return Google Protocol Buffer object corresponding to IEvent
	 */
	public static GpEvent createGpbEvent(IEvent e) {
		if (e == null) 
			return null;
		
		PdpProtos.GpEvent.Builder gpEvent = PdpProtos.GpEvent.newBuilder();
		if (e.getName() != null)
			gpEvent.setName(e.getName());
		
		Map<String, String> map = e.getParameters();
		if (map != null && !map.isEmpty()) {
			Set<String> keys = map.keySet();
			for (String key:keys) {
				String value = map.get(key);
				PdpProtos.GpEvent.GpMapEntry.Builder entry = PdpProtos.GpEvent.GpMapEntry.newBuilder();
				entry.setKey(key);
				entry.setValue(value);
				
				gpEvent.addMapEntry(entry);
			}
		}
		
		return gpEvent.build();
	}

	@Override
	public String toString() {
		return "EventBasic [_name=" + _name + ", _map=" + _map
				+ ", _timestamp=" + _timestamp + "]";
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			EventBasic o = (EventBasic)obj;
			//TODO check if timestamp should be checked
			isEqual = CompareUtil.areObjectsEqual(_name, o.getName())
					&& CompareUtil.areMapsEqual(_map, o.getParameters());
		}
		return isEqual;
	}
}
