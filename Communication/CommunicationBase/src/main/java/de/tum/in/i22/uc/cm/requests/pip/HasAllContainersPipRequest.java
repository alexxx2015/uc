package de.tum.in.i22.uc.cm.requests.pip;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.server.PipProcessor;

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