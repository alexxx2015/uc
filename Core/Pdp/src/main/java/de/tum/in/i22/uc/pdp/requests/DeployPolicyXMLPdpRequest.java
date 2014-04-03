package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class DeployPolicyXMLPdpRequest extends PdpRequest<IStatus> {
	private final String _policyPath;

	public DeployPolicyXMLPdpRequest(String policyPath) {
		_policyPath= policyPath;
	}

	@Override
	public IStatus process(PdpProcessor processor) {
		return processor.deployPolicyXML(_policyPath);
	}

}
