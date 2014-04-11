package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class StopSimulationPipRequest extends PipRequest<IStatus> {

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.stopSimulation();
	}

}
