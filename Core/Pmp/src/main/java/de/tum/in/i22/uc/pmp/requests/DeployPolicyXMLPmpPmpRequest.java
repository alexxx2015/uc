package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DeployPolicyXMLPmpPmpRequest extends PmpRequest<IStatus> {
	private final String _policyPath;

	public DeployPolicyXMLPmpPmpRequest(String policyPath) {
		_policyPath= policyPath;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.deployPolicyXMLPmp(_policyPath);
	}

}
