package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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
	private final Map<String, String> _parameters = new HashMap<>();
	private long _timestamp;

	public EventBasic(String name, Map<String, String> map) {
		_name = name;
		if (map != null) {
			_parameters.putAll(map);
			_pep = _parameters.get(PEP_PARAMETER_KEY);
		}
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
			Iterator<GpMapEntry> it = gpEvent.getMapEntryList().iterator();
			while (it.hasNext()) {
				GpMapEntry entry = it.next();
				_parameters.put(entry.getKey(), entry.getValue());
			}
		}

		_pep = _parameters.get(PEP_PARAMETER_KEY);

		// Insert timestamp
		if (gpEvent.hasTimestamp() && gpEvent.getTimestamp() != null && !gpEvent.getTimestamp().isEmpty())
			_timestamp = Long.valueOf(gpEvent.getTimestamp());
		else
			_timestamp = 0;
	}

	public void addParameter(String key, String value) {
		_parameters.put(key, value);
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
		return _parameters;
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
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_name", _name)
				.add("_pep", _pep)
				.add("_isActual", _isActual)
				.add("_parameters", _parameters)
				.add("_timestamp", _timestamp)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			EventBasic o = (EventBasic)obj;
			//TODO check if timestamp should be checked
			isEqual = Objects.equals(_name, o._name)
					&& Objects.equals(_isActual, o._isActual)
					&& Objects.equals(_parameters, o._parameters);
		}
		return isEqual;
	}


	@Override
	public int hashCode() {
		return Objects.hash(_name, _isActual, _parameters);
	}

	public String niceString() {
		return _name + _parameters;
	}
}
