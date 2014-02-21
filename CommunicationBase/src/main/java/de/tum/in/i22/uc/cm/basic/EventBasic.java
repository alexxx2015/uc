package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent.GpMapEntry;

public class EventBasic implements IEvent {

	public static final String PEP_PARAMETER_KEY = "PEP";

	public static final String PREFIX_SEPARATOR = "_";

	private String _name = null;
	private String _pep = null;
	private boolean _isActual = false;
	private Map<String, String> _map = null;
	private long _timestamp;

	public EventBasic() {
		_map = new HashMap<>();
	}

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		_map = map;
		setPep(map);
	}

	public EventBasic(String name, Map<String, String> map, boolean isActual) {
		this(name, map);
		_isActual = isActual;
	}

	public EventBasic(GpEvent gpEvent) {
		if (gpEvent == null)
			return;

		if (gpEvent.hasName())
			_name = gpEvent.getName();

		if (gpEvent.hasIsActual())
			_isActual = gpEvent.getIsActual();

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

		setPep(_map);

		// Insert timestamp
		if (gpEvent.hasTimestamp() && gpEvent.getTimestamp() != null && !gpEvent.getTimestamp().isEmpty())
			_timestamp = Long.valueOf(gpEvent.getTimestamp());
		else
			_timestamp = 0;
	}

	public void addParameter(String key, String value) {
		_map.put(key, value);
	}

	@Override
	public long getTimestamp() {
		return _timestamp;
	}

	@Override
	public String getPrefixedName() {
		if (_pep == null) {
			return _name;
		}

		return _pep + PREFIX_SEPARATOR + _name;
	}

	@Override
	public String getName() {
		return _name;
	}

	private void setPep(Map<String,String> parameters) {
		if (parameters != null) {
			_pep = parameters.get(PEP_PARAMETER_KEY);
		}
	}

	@Override
	public String getPep() {
		return _pep;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setActual(boolean isActual) {
		_isActual = isActual;
	}

	@Override
	public boolean isActual() {
		return _isActual;
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
		if (e.getPrefixedName() != null)
			gpEvent.setName(e.getName());

		gpEvent.setIsActual(e.isActual());

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
		return "EventBasic [_name=" + _name + ", _pep=" + _pep + ", _isActual=" + _isActual
				+ ", _map=" + _map + ", _timestamp=" + _timestamp + "]";
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			EventBasic o = (EventBasic)obj;
			//TODO check if timestamp should be checked
			isEqual = CompareUtil.areObjectsEqual(_name, o.getPrefixedName())
					&& _isActual == o.isActual()
					&& CompareUtil.areMapsEqual(_map, o.getParameters());
		}
		return isEqual;
	}
}
