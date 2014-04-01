package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class NotifyActualEventPipRequest extends PipRequest<IStatus> {

	private final IEvent _event;

	public NotifyActualEventPipRequest(IEvent event) {
		_event = event;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.notifyActualEvent(_event);
	}

}
