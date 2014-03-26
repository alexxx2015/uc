package de.tum.in.i22.uc.pdp;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.GenericHandler;
import de.tum.in.i22.uc.cm.requests.PdpRequest;
import de.tum.in.i22.uc.pdp.core.Event;
import de.tum.in.i22.uc.pdp.core.IPolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;


public class PdpHandler extends GenericHandler<PdpRequest> implements IAny2Pdp {

	private final IPolicyDecisionPoint _lpdp;

	private static PdpHandler _instance = new PdpHandler();

	private boolean _initialized = false;

	private IAny2Pmp _pmp;
	private IAny2Pip _pip;

	private PdpHandler() {
		_lpdp = PolicyDecisionPoint.getInstance();
	}

	public static PdpHandler getInstance() {
		return _instance;
	}

	@Override
	public void init(IAny2Pip pip, IAny2Pmp pmp) {
		if (!_initialized) {
			_initialized = true;
			_pmp = pmp;
			_pip = pip;
		}
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		return _lpdp.notifyEvent(new Event(event)).getResponse();
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Superstar.
		return false;
	}

	@Override
	public Object process(PdpRequest request) {
		Object result = null;

		switch (request.getType()) {
			case REGISTER_PXP:
				result = registerPxp(request.getPxp());
			case NOTIFY_EVENT:
				result = notifyEvent(request.getEvent());
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
}
