package de.tum.in.i22.pmp.core;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PmpRequest;


public class PmpHandler implements IAny2Pmp {

	private IAny2Pip _pip;
	private IAny2Pdp _pdp;

	@Override
	public void init(IAny2Pip pip, IAny2Pdp pdp) {
		_pip = pip;
		_pdp = pdp;
	}

	@Override
	public Object process(PmpRequest request) {
		Object result = null;

		switch (request.getType()) {
			case DEPLOY_MECHANISM:
				result = _pdp.deployMechanism(request.getMechanism());
				break;
			case EXPORT_MECHANISM:
				result = _pdp.exportMechanism(request.getStringParameter());
				break;
			case REVOKE_MECHANISM:
				result = _pdp.revokeMechanism(request.getStringParameter());
				break;
			default:
				throw new RuntimeException("Method " + request.getType() + " is not supported!");
		}

		return result;
	}

}
