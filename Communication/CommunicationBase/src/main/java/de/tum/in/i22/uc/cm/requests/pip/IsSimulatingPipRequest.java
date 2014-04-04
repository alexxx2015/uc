package de.tum.in.i22.uc.cm.requests.pip;

import de.tum.in.i22.uc.cm.server.PipProcessor;

public class IsSimulatingPipRequest extends PipRequest<Boolean> {

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.isSimulating();
	}

}
