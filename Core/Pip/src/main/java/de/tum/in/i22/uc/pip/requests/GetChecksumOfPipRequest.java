package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetChecksumOfPipRequest extends PipRequest<IChecksum> {
	private final IData _data;
	
	public GetChecksumOfPipRequest(IData data) {
		_data=data;
	}
	
	@Override
	public IChecksum process(PipProcessor processor) {
		return processor.getChecksumOf(_data);
	}

}
