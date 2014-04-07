package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PmpProcessor;

public class RemotePolicyTransferPmpRequest extends PmpRequest<IStatus> {
	private final Set<String> _policies;

	public RemotePolicyTransferPmpRequest(Set<String> policies) {
		_policies = policies;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.remotePolicyTransfer(_policies);
	}
}
