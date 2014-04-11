package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

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
