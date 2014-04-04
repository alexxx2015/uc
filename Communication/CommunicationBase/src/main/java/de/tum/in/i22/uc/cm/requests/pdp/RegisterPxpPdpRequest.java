package de.tum.in.i22.uc.cm.requests.pdp;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class RegisterPxpPdpRequest extends PdpRequest<Boolean> {
	private final IPxpSpec _pxp;

	public RegisterPxpPdpRequest(IPxpSpec pxp) {
		_pxp = pxp;
	}

	@Override
	public Boolean process(PdpProcessor processor) {
		//TODO:add nullness checks
		return processor.registerPxp(_pxp);
	}

}
