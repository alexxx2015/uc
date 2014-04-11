package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class HasAllContainersPipRequest extends PipRequest<Boolean> {
	private final Set<IContainer> _containers;

	public HasAllContainersPipRequest(Set<IContainer> container) {
		_containers = container;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.hasAllContainers(_containers);
	}
}
