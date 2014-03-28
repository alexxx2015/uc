package de.tum.in.i22.uc.cm.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PdpRequest;

public abstract class PdpProcessor implements Processor<PdpRequest>, IAny2Pdp  {

	protected static Logger _logger = LoggerFactory.getLogger(PdpProcessor.class);

	private IAny2Pmp _pmp;
	private IAny2Pip _pip;

	private boolean _initialized = false;

	@Override
	public void init(IAny2Pip pip, IAny2Pmp pmp) {
		if (!_initialized) {
			_initialized = true;
			_pmp = pmp;
			_pip = pip;
		}
	}

	@Override
	public final Object process(PdpRequest request) {
		Object result = null;

		switch (request.getType()) {
			case REGISTER_PXP:
				result = registerPxp(request.getPxp());
			case NOTIFY_EVENT:
				result = notifyEvent(request.getEvent());

				// TODO: As of now unconditionally notifying event to PIP.
				_pip.notifyActualEvent(request.getEvent());
				break;
			case DEPLOY_MECHANISM:
				break;
			case DEPLOY_POLICY:
				break;
			case EXPORT_MECHANISM:
				break;
			case LIST_MECHANISMS:
				break;
			case REVOKE_MECHANISM:
				break;
			default:
				throw new RuntimeException("Method " + request.getType() + " is not supported!");
		}

		return result;
	}

	protected IAny2Pmp getPmp() {
		return _pmp;
	}

	protected IAny2Pip getPip() {
		return _pip;
	}
}
