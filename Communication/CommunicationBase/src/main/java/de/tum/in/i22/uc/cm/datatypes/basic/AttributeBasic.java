package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IAttribute;


/**
 * A basic attribute.
 *
 * Parameter <V> describes the type of the attribute's value.
 *
 * @author Florian Kelbert
 *
 * @param <V>
 */
public class AttributeBasic<V> implements IAttribute<V> {

	private final EAttributeName _name;
	private final V _value;
	private final Class<V> _valueType;

	public AttributeBasic(EAttributeName name, V value, Class<V> valueType) {
		_name = name;
		_value = value;
		_valueType = valueType;
	}

	@Override
	public EAttributeName getName() {
		return _name;
	}

	@Override
	public V getValue() {
		return _value;
	}

	@Override
	public Class<V> getValueType() {
		return _valueType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AttributeBasic) {
			AttributeBasic<?> other = (AttributeBasic<?>) obj;
			return Objects.equals(_name, other._name)
					&& Objects.equals(_value, other._value);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _value);
	}


	public enum EAttributeName {
		TYPE,
		OWNER;
	}
}
