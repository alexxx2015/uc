package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.IEvent;

public class EventBasic implements IEvent {

	public static final String PEP_PARAMETER_KEY = "PEP";

	private static final String PREFIX_SEPARATOR = "_";

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
		return _name + (_isActual ? "[Actual]" : "[Desired]") +_parameters;
	}
}
