package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;

public class NotifyEventPdpRequest extends PdpRequest<IResponse> {
	private final IEvent _event;

	public NotifyEventPdpRequest(IEvent event) {
		_event = event;
	}

	@Override
	public IResponse process(PdpProcessor processor) {
		IResponse res = processor.notifyEventSync(_event);
		processor.getPip().notifyActualEvent(_event);
		return res;
	}

}
