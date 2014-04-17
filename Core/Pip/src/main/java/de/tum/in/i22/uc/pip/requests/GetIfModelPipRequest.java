package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class GetIfModelPipRequest extends PipRequest<String> {

	@Override
	public String process(PipProcessor processor) {
		return processor.toString();
	}
}
