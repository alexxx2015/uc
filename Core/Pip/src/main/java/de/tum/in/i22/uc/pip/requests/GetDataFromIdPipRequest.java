package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetDataFromIdPipRequest extends PipRequest<IData> {

	private final String _id;

	public GetDataFromIdPipRequest(String id) {
		_id = id;
	}

	@Override
	public IData process(PipProcessor processor) {
		return processor.getDataFromId(_id);
	}
}
