package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class StopSimulationPipRequest extends PipRequest<IStatus> {

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.stopSimulation();
	}

}
