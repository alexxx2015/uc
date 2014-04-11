package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;

public class DataBasic implements IData {

	private String _id;

	public DataBasic() {
		// generate unique id
		_id = UUID.randomUUID().toString();
	}

	public DataBasic(String id) {
		this();
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
		return Objects.hash(_id);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_id", _id)
				.toString();
	}

}
