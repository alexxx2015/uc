package de.tum.in.i22.uc.pip.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetStructureOfPipRequest extends PipRequest<Map<String, Set<IData>>> {

	private final IData _data;
	
	public GetStructureOfPipRequest(IData data) {
		_data=data;
	}

	@Override
	public Map<String, Set<IData>> process(PipProcessor processor) {
		return processor.getStructureOf(_data);
	}
}
