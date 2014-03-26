package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

public abstract class GenericPdpHandler extends GenericHandler<PdpRequest> implements IAny2Pdp  {
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
