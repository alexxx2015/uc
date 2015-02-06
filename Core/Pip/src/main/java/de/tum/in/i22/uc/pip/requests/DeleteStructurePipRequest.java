package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class DeleteStructurePipRequest extends PipRequest<Boolean> {
	private final IData _data;
	
	public DeleteStructurePipRequest(IData data) {
		_data=data;
	}
	
	@Override
	public Boolean process(PipProcessor processor) {
		return processor.deleteStructure(_data);
	}

}
