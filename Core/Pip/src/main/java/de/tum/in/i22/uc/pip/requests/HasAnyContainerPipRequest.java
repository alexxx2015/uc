package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.processing.PipProcessor;


public class HasAnyContainerPipRequest extends PipRequest<Boolean> {

	private final Set<IName> _containers;

	public HasAnyContainerPipRequest(Set<IName> containers) {
		_containers = containers;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.hasAnyContainer(_containers);
	}
}
