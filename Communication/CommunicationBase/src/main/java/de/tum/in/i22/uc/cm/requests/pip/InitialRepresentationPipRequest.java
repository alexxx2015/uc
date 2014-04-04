package de.tum.in.i22.uc.cm.requests.pip;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class InitialRepresentationPipRequest extends PipRequest<IStatus> {
	private final IName _name;
	private final Set<IData> _data;

	public InitialRepresentationPipRequest(IName containerName, Set<IData> data) {
		_name = containerName;
		_data = data;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.initialRepresentation(_name, _data);
	}
}
