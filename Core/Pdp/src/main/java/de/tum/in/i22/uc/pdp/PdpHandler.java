package de.tum.in.i22.uc.pdp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPolicyDecisionPoint;

public class PdpHandler extends PdpProcessor {

	private final IPolicyDecisionPoint _lpdp;

	public PdpHandler() {
		_lpdp = PolicyDecisionPoint.getInstance();
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
	public Map<String, ArrayList<IMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
//		return _lpdp.listDeployedMechanisms();

		return Collections.EMPTY_MAP;
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		return PxpManager.getInstance().registerPxp(pxp);
	}

	@Override
	public IResponse notifyEventAsync(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		return _lpdp.notifyEvent(new Event(event)).getResponse();
	}
}
