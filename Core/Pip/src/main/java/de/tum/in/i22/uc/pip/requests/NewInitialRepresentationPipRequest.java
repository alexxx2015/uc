package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class NewInitialRepresentationPipRequest extends PipRequest<IData> {
	private final IName _name;

	public NewInitialRepresentationPipRequest(IName containerName) {
		_name = containerName;
	}

	@Override
	public IData process(PipProcessor processor) {
		return processor.newInitialRepresentation(_name);
	}
}
