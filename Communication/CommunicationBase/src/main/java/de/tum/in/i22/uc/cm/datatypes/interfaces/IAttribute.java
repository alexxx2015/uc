package de.tum.in.i22.uc.cm.datatypes.interfaces;

import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic.EAttributeName;

public interface IAttribute<V> {
	public EAttributeName getName();
	public V getValue();
	public Class<V> getValueType();
}
