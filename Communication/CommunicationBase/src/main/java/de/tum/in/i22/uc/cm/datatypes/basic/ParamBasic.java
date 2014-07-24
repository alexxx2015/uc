package de.tum.in.i22.uc.cm.datatypes.basic;

import java.io.Serializable;
import java.util.Objects;

public class ParamBasic implements Serializable {
	private static final long serialVersionUID = -7061921148298856812L;

	private final String _name;
	private final String _value;

	public ParamBasic(String name, String value) {
		if (name == null || value == null)
			throw new NullPointerException("Name and/or value may not be null");
		_name = name;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this.getClass())
				.add("_name", _name)
				.add("_value", _value)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ParamBasic) {
			ParamBasic o = (ParamBasic) obj;
			return Objects.equals(_name, o._name)
					&& Objects.equals(_value, o._value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _value);
	}
}
