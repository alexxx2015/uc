package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;

public class ContainerBasic implements IContainer {
	private final String _id;

	public ContainerBasic() {
		this(null);
	}

	public ContainerBasic(String id) {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}

		_id = id;
	}

	@Override
	public String getId() {
		return _id;
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
