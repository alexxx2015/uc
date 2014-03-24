package de.tum.in.i22.uc.pmp;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.GenericHandler;
import de.tum.in.i22.uc.cm.requests.PmpRequest;


public class PmpHandler extends GenericHandler<PmpRequest> implements IAny2Pmp {

	private IAny2Pip _pip;
	private IAny2Pdp _pdp;
	private boolean _initialized = false;

	private static final PmpHandler _instance = new PmpHandler();

	private PmpHandler(){
	}

	public static PmpHandler getInstance(){
		return _instance;
	}

	@Override
	public void init(IAny2Pip pip, IAny2Pdp pdp) {
		if (!_initialized) {
			_initialized = true;
			_pip = pip;
			_pdp = pdp;
		}
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
