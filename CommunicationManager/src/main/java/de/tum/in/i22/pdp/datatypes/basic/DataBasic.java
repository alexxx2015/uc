package de.tum.in.i22.pdp.datatypes.basic;

import de.tum.in.i22.pdp.datatypes.IData;

public class DataBasic implements IData {

	private String _id;
	
	public DataBasic(String id) {
		super();
		_id = id;
	}

	@Override
	public String getId() {
		return _id;
	}

}
