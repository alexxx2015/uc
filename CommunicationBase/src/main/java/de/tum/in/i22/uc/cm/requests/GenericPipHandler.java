package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class GenericPipHandler extends GenericHandler<PipRequest> implements IAny2Pip {

	private IAny2Pdp _pdp;
	private IAny2Pmp _pmp;

	private boolean _initialized = false;

	@Override
	public void init(IAny2Pdp pdp, IAny2Pmp pmp) {
		if (!_initialized) {
			_pdp = pdp;
			_pmp = pmp;
			_initialized = true;
		}
	}

	@Override
	public final Object process(PipRequest request) {
		Object result = null;

		switch(request.getType()) {
			case EVALUATE_PREDICATE:
				break;
			case GET_CONTAINER_FOR_DATA:
				break;
			case GET_DATA_IN_CONTAINER:
				break;
			case HAS_ALL_CONTAINERS:
				break;
			case HAS_ALL_DATA:
				break;
			case HAS_ANY_CONTAINER:
				break;
			case HAS_ANY_DATA:
				break;
			case NOTIFY_ACTUAL_EVENT:
				result = notifyActualEvent(request.getEvent());
				break;
			case NOTIFY_DATA_TRANSFER:
				break;
			case UPDATE_INFORMATION_FLOW_SEMANTICS:
				break;
			default:
				throw new RuntimeException("Method " + request.getType() + " is not supported!");
		}

		return result;
	}

	protected IAny2Pmp getPmp() {
		return _pmp;
	}

	protected IAny2Pdp getPdp() {
		return _pdp;
	}
}
