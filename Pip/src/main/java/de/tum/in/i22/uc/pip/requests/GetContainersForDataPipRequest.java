package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetContainersForDataPipRequest extends PipRequest<Set<IContainer>> {

	private final IData _data;

	public GetContainersForDataPipRequest(IData data) {
		_data = data;
	}

	@Override
	public Set<IContainer> process(PipProcessor processor) {
		return processor.getContainersForData(_data);
	}

}
