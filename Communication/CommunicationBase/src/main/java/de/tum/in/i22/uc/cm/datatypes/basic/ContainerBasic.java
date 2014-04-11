package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic.EAttributeName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IAttribute;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;

public class ContainerBasic implements IContainer {
	private final String _id;

	private final Map<EAttributeName,IAttribute<?>> _attributes;

	public ContainerBasic() {
		this((String) null);
	}

	public ContainerBasic(IAttribute<?> ... attributes) {
		this(null, attributes);
	}

	public ContainerBasic(String id, IAttribute<?> ... attributes) {
		if (id == null || id.isEmpty()) {
			id = UUID.randomUUID().toString();
		}

		_id = id;

		_attributes = new HashMap<>();
		for (IAttribute<?> attr : attributes) {
			_attributes.put(attr.getName(), attr);
		}
	}

	@Override
	public String getId() {
		return _id;
	}

	public boolean hasAttribute(EAttributeName name) {
		return _attributes.keySet().contains(name);
	}

	public IAttribute<?> getAttribute(EAttributeName name) {
		return _attributes.get(name);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ContainerBasic)
				&& Objects.equals(_id, ((ContainerBasic) obj)._id);
	}

	@Override
	public int hashCode() {
		return _id.hashCode();
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_id", _id)
				.toString();
	}

}
