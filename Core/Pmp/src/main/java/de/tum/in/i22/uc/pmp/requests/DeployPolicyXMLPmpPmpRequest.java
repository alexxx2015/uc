package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DeployPolicyXMLPmpPmpRequest extends PmpRequest<IStatus> {
	private final XmlPolicy _xmlPolicy;

	public DeployPolicyXMLPmpPmpRequest(XmlPolicy xmlPolicy) {
		_xmlPolicy= xmlPolicy;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.deployPolicyXMLPmp(_xmlPolicy);
	}

}
