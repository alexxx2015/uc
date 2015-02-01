package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class RemotePolicyTransferPmpRequest extends PmpRequest<IStatus> {
	private final String _policy;
	private final String _from;

	public RemotePolicyTransferPmpRequest(String policy, String from) {
		_policy = policy;
		_from = from;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.remotePolicyTransfer(_policy, _from);
	}
}
