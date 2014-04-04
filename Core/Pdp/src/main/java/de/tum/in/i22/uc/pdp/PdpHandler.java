package de.tum.in.i22.uc.pdp;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
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
	public IMechanism exportMechanism(String par) {
		//TODO: functionality not yet implemented in the pdp
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		boolean b=_lpdp.revokePolicy(policyName);
		return (b==true?
				new StatusBasic(EStatus.OKAY):
					new StatusBasic(EStatus.ERROR,"revokePolicy failed"));
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		//TODO: sanitize inputs
		boolean b=_lpdp.revokeMechanism(policyName, mechName);
		return (b==true?
				new StatusBasic(EStatus.OKAY):
					new StatusBasic(EStatus.ERROR,"revokeMechanism failed"));
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		return (_lpdp.deployPolicyURI(policyFilePath) ?
				new StatusBasic(EStatus.OKAY):
					new StatusBasic(EStatus.ERROR,"deploy policy failed"));
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		return (_lpdp.deployPolicyXML(XMLPolicy) ?
				new StatusBasic(EStatus.OKAY):
					new StatusBasic(EStatus.ERROR,"deploy policy failed"));
	}


	@Override
	public Map<String, List<String>> listMechanisms() {
		return _lpdp.listDeployedMechanisms();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		return PxpManager.getInstance().registerPxp(pxp);
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_lpdp.notifyEvent(new Event(event));
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		return _lpdp.notifyEvent(new Event(event)).getResponse();
	}
}
