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

	private static PmpHandler _instance = null;

	private PmpHandler(){
	}

	public static PmpHandler getInstance(){
		if (_instance==null) _instance = new PmpHandler();
		return _instance;
	}

	@Override
	public void init(IAny2Pip pip, IAny2Pdp pdp) {
		_initialized = true;
		if (!_initialized) {
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
