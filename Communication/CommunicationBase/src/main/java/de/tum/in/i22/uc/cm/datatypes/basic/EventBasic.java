package de.tum.in.i22.uc.cm.datatypes.basic;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;

public class EventBasic implements IEvent, Serializable {
	private static final long serialVersionUID = 4317254908091570374L;
	public static final String PEP_PARAMETER_KEY = Settings.getInstance().getPep();

	private final String _name;
	private String _pep;
	private boolean _allowImpliesActual = false;
	private final boolean _isActual;
	private final Map<String, String> _parameters = new HashMap<>();
	private long _timestamp;

	public EventBasic(String name, Map<String, String> params) {
		this(name, params, false, 0);
	}

	public EventBasic(String name, Map<String, String> params, boolean isActual) {
		this(name, params, isActual, 0);
	}

	public EventBasic(String name, Map<String, String> params, boolean isActual, long timeStamp) {
		_name = name;
		if (params != null) {
			_parameters.putAll(params);
			_pep = _parameters.get(PEP_PARAMETER_KEY);

			/*
			 * If the event has an allowImpliesActual parameter, use it.
			 * Otherwise, fall back to default value.
			 */
			String allowImpliesActual = _parameters.get(Settings.PROP_NAME_allowImpliesActual);
			_allowImpliesActual = allowImpliesActual != null
					? Boolean.valueOf(allowImpliesActual)
					: Settings.getInstance().getAllowImpliesActual();
		}

		_timestamp = timeStamp;
		_isActual = isActual;
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
	public String getPep() {
		return _pep;
	}

	@Override
	public boolean isActual() {
		return _isActual;
	}

	@Override
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(_parameters);
	}

	@Override
	public String getParameterValue(String name) {
		return name == null ? null : _parameters.get(name);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_name", _name)
				.add("_pep", _pep)
				.add("_isActual", _isActual)
				.add("_parameters", _parameters)
				.add("_timestamp", _timestamp)
				.add(Settings.PROP_NAME_allowImpliesActual, _allowImpliesActual)
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

	@Override
	public boolean allowImpliesActual() {
		return _allowImpliesActual;
	}
}
