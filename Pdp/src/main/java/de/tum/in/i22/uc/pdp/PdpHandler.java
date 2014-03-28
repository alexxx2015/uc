package de.tum.in.i22.uc.pdp;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.pdp.core.Event;
import de.tum.in.i22.uc.cm.pdp.core.IPdpMechanism;
import de.tum.in.i22.uc.cm.pdp.core.IPolicyDecisionPoint;
import de.tum.in.i22.uc.cm.processor.PdpProcessor;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;


public class PdpHandler extends PdpProcessor {
	private final IPolicyDecisionPoint _lpdp;

	public PdpHandler() {
		_lpdp = PolicyDecisionPoint.getInstance();
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
		return _lpdp.listDeployedMechanisms();
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Superstar.
		return false;
	}
}
