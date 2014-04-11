package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;

public class ContainerBasic implements IContainer {
	private String _classValue;
	private String _id;

	public ContainerBasic() {
		this(null, null);
	}

	public ContainerBasic(String classValue, String id) {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		_classValue = classValue;
		_id = id;
	}


	@Override
	public String getClassValue() {
		return _classValue;
	}

	@Override
	public String getId() {
		return _id;
	}



	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof ContainerBasic) {
			ContainerBasic o = (ContainerBasic)obj;
			isEqual = Objects.equals(_id, o._id) &&
					Objects.equals(_classValue, o._classValue);
		}

		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id, _classValue);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_id", _id)
				.add("_classValue", _classValue)
				.toString();
	}

}
