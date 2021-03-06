package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

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
