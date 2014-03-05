package de.tum.in.i22.uc.cm.basic;

import java.util.Objects;
import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpData;

public class DataBasic implements IData {

	private String _id;

	public DataBasic() {
		// generate unique id
		_id = UUID.randomUUID().toString();
	}

	public DataBasic(String id) {
		super();
		_id = id;
	}

	public DataBasic(GpData gpData) {
		if (gpData == null)
			return;

		if (gpData.hasId())
			_id = gpData.getId();
	}

	@Override
	public String getId() {
		if (_id == null) {
			_id = UUID.randomUUID().toString();
		}
		return _id;
	}

	/**
	 *
	 * @return Google Protocol Buffer object corresponding to IData
	 */
	public static GpData createGpbData(IData data) {
		GpData.Builder gp = GpData.newBuilder();
		gp.setId(data.getId());
		return gp.build();
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
