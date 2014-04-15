package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetDataInContainerPipRequest extends PipRequest<Set<IData>> {

	private final IName _containerName;

	public GetDataInContainerPipRequest(IName containerName) {
		_containerName = containerName;
	}

	@Override
	public Set<IData> process(PipProcessor processor) {
		return processor.getDataInContainer(_containerName);
	}
}
