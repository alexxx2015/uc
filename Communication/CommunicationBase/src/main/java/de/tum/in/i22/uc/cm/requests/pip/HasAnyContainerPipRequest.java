package de.tum.in.i22.uc.cm.requests.pip;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.server.PipProcessor;

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
