package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.pdp.PxpManager;

public class RegisterPxpPdpRequest extends PdpRequest<Boolean> {
	private final IPxpSpec _pxp;

	public RegisterPxpPdpRequest(IPxpSpec pxp) {
		_pxp = pxp;
	}

	@Override
	public Boolean process(PdpProcessor processor) {
		//TODO:add nullness checks
		return PxpManager.getInstance().registerPxp(_pxp);
	}

}
