package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class SpecifyPolicyForPmpRequest extends PmpRequest<IStatus> {
	private final String _dataClass;
	private final Set<IContainer> _representations;

	public SpecifyPolicyForPmpRequest(Set<IContainer> representations, String dataClass) {
		_dataClass=dataClass;
		_representations=representations;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.specifyPolicyFor(_representations, _dataClass);
	}
}
