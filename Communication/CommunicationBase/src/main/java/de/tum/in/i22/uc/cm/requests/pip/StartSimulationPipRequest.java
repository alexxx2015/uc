package de.tum.in.i22.uc.cm.requests.pip;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class StartSimulationPipRequest extends PipRequest<IStatus> {

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.startSimulation();
	}

}
