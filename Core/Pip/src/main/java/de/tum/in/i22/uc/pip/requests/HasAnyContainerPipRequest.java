package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class HasAnyContainerPipRequest extends PipRequest<Boolean> {

	private final Set<IContainer> _containers;

	public HasAnyContainerPipRequest(Set<IContainer> containers) {
		_containers = containers;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.hasAnyContainer(_containers);
	}
}
