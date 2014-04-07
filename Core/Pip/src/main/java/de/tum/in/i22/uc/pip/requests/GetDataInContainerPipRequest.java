package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class GetDataInContainerPipRequest extends PipRequest<Set<IData>> {

	private final IContainer _container;

	public GetDataInContainerPipRequest(IContainer container) {
		_container = container;
	}

	@Override
	public Set<IData> process(PipProcessor processor) {
		return processor.getDataInContainer(_container);
	}
}
