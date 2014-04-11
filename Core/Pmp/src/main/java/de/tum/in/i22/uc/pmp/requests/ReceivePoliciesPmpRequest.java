package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class ReceivePoliciesPmpRequest extends PmpRequest<IStatus> {
	private final Set<String> _policies;

	public ReceivePoliciesPmpRequest(Set<String> policies) {
		_policies = policies;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.receivePolicies(_policies);
	}
}
