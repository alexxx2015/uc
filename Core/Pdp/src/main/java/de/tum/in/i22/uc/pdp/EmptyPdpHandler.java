package de.tum.in.i22.uc.pdp;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class EmptyPdpHandler extends PdpProcessor {

	@Override
	public void notifyEventAsync(IEvent event) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IMechanism exportMechanism(String par) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		throw new UnsupportedOperationException("No PdpHandler deployed.");
	}

}
