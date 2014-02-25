package de.tum.in.i22.uc.cm.basic;

import java.util.UUID;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;

public class ContainerBasic implements IContainer {
	private String _classValue;
	private String _id;

	public ContainerBasic() {}

	public ContainerBasic(String classValue, String id) {
		super();
		_classValue = classValue;
		_id = id;
	}

	public ContainerBasic(GpContainer gpContainer) {
		if (gpContainer == null) {
			return;
		}

		if (gpContainer.hasClassValue()) {
			_classValue = gpContainer.getClassValue();
		}

		if (gpContainer.hasId()) {
			_id = gpContainer.getId();
		}
	}

	@Override
	public String getClassValue() {
		return _classValue;
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
	 * @param container
	 * @return Google Protocol Buffer object corresponding to IContainer
	 */
	public static GpContainer createGpbContainer(IContainer container) {
		GpContainer.Builder gp = GpContainer.newBuilder();
		gp.setClassValue(container.getClassValue());
		gp.setId(container.getId());
		return gp.build();
	}

	// TODO, FK: I have the feeling that equals() and hashCode() break
	// the general contract between the two (see official javadoc). Double check.

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof ContainerBasic) {
			ContainerBasic o = (ContainerBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_id, o.getId()) &&
					CompareUtil.areObjectsEqual(_classValue, o.getClassValue());
		}

		return isEqual;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return "ContainerBasic [_classValue=" + _classValue + ", _id=" + _id
				+ "]";
	}

}
