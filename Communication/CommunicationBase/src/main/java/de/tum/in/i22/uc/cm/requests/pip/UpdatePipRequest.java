package de.tum.in.i22.uc.cm.requests.pip;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class UpdatePipRequest extends PipRequest<IStatus> {

	private final IEvent _event;

	public UpdatePipRequest(IEvent event) {
		_event = event;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.update(_event);
	}

}
