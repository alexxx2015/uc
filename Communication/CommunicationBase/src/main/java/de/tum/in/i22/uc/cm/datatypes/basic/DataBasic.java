package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;

public class DataBasic implements IData {

	private final String _id;

	public DataBasic() {
		this(UUID.randomUUID().toString());
	}

	public DataBasic(String id) {
		_id = id;
	}


	@Override
	public String getId() {
		return _id;
	}


	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof DataBasic) {
			isEqual = Objects.equals(_id, ((DataBasic) obj)._id);
		}

		return isEqual;
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
