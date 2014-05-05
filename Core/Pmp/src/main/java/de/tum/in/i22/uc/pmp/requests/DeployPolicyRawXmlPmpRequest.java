package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DeployPolicyRawXmlPmpRequest extends PmpRequest<IStatus> {
	private final String _policy;

	public DeployPolicyRawXmlPmpRequest(String policy) {
		_policy = policy;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.deployPolicyRawXMLPmp(_policy);
	}
}
