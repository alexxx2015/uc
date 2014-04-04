package de.tum.in.i22.uc.cm.requests.pdp;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class RegisterPxpPdpRequest extends PdpRequest<Boolean> {
	private final PxpSpec _pxp;

	public RegisterPxpPdpRequest(PxpSpec pxp) {
		_pxp = pxp;
	}

	@Override
	public Boolean process(PdpProcessor processor) {
		//TODO:add nullness checks
		return processor.registerPxp(_pxp);
	}

}
