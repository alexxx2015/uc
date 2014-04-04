package de.tum.in.i22.uc.cm.requests.pdp;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class RevokePolicyPdpRequest extends PdpRequest<IStatus> {
	private final String _policyName;

	public RevokePolicyPdpRequest(String policyName) {
		_policyName= policyName;
	}

	@Override
	public IStatus process(PdpProcessor processor) {
		return processor.revokePolicy(_policyName);
	}

}
