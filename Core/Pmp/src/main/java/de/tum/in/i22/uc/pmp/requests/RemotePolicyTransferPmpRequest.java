package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class RemotePolicyTransferPmpRequest extends PmpRequest<IStatus> {
	private final XmlPolicy _policy;
	private final String _from;

	public RemotePolicyTransferPmpRequest(XmlPolicy policy, String from) {
		_policy = policy;
		_from = from;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.incomingPolicyTransfer(_policy, _from);
	}
}
