package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class InitialRepresentationPipRequest extends PipRequest<IStatus> {
	private IName name;
	private Set<IData> data;

	public InitialRepresentationPipRequest(IName containerName, Set<IData> data) {
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.initialRepresentation(name, data);
	}
}
