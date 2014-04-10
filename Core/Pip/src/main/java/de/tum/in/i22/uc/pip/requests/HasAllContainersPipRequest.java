package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class HasAllContainersPipRequest extends PipRequest<Boolean> {
	private final Set<IName> _containers;

	public HasAllContainersPipRequest(Set<IName> container) {
		_containers = container;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.hasAllContainers(_containers);
	}
}
