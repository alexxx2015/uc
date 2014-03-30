package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.PipProcessor;
import de.tum.in.i22.uc.pip.PipRequest;

public class NotifyActualEventPipRequest extends PipRequest<IStatus> {

	public NotifyActualEventPipRequest(IEvent event) {
		super(event);
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.notifyActualEvent(_event);
	}

}
