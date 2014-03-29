package de.tum.in.i22.uc.cm.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PmpRequest;

public abstract class PmpProcessor implements Processor<PmpRequest<?>>, IAny2Pmp {
	protected static Logger _logger = LoggerFactory.getLogger(PmpProcessor.class);

	private IAny2Pip _pip;
	private IAny2Pdp _pdp;
	private boolean _initialized = false;

	@Override
	public void init(IAny2Pip pip, IAny2Pdp pdp) {
		if (!_initialized) {
			_initialized = true;
			_pip = pip;
			_pdp = pdp;
		}
	}

	@Override
	public final Object process(PmpRequest<?> request) {
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

	public IAny2Pdp getPdp() {
		return _pdp;
	}

	public IAny2Pip getPip() {
		return _pip;
	}
}
