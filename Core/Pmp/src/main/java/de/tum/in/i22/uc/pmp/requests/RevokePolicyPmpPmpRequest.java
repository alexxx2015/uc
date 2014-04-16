package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class RevokePolicyPmpPmpRequest extends PmpRequest<IStatus> {
	private final String _policyName;

	public RevokePolicyPmpPmpRequest(String policyName) {
		_policyName= policyName;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.revokePolicyPmp(_policyName);
	}

}
