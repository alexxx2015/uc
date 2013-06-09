package de.tum.in.i22.pdp.datatypes.basic;

import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpData;

public class DataBasic implements IData {

	private String _id;
	
	public DataBasic(String id) {
		super();
		_id = id;
	}
	
	public DataBasic(GpData gpData) {
		if (gpData == null) 
			return;
		_id = gpData.getId();
	}

	@Override
	public String getId() {
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
		if (obj != null && this.getClass() == obj.getClass()) {
			DataBasic o = (DataBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(_id, o.getId());
		}
		
		return isEqual;
	}

}
