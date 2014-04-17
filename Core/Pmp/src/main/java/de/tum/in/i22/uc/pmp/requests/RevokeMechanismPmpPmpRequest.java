package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class RevokeMechanismPmpPmpRequest extends PmpRequest<IStatus> {
	private final String _policyName;
	private final String _mechName;

	public RevokeMechanismPmpPmpRequest(String policyName, String mechName) {
		_policyName= policyName;
		_mechName= mechName;

	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.revokeMechanismPmp(_policyName, _mechName);
	}

}
