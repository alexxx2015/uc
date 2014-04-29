package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class FlattenStructurePipRequest extends PipRequest<Set<IData>> {

	private final IData _data;
	
	public FlattenStructurePipRequest(IData data) {
		_data=data;
	}

	@Override
	public Set<IData> process(PipProcessor processor) {
		return processor.flattenStructure(_data);
	}
}
