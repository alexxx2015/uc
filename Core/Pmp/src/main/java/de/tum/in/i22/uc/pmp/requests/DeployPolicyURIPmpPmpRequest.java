package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DeployPolicyURIPmpPmpRequest extends PmpRequest<IStatus> {
	private final String _policyPath;

	public DeployPolicyURIPmpPmpRequest(String policyPath) {
		_policyPath= policyPath;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.deployPolicyURIPmp(_policyPath);
	}

}
