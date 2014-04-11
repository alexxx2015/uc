package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;


/**
 *
 * @author Florian Kelbert & Enrico Lovat
 *
 */
public class NotifyEventPdpRequest extends PdpRequest<IResponse> {
	private final IEvent _event;

	public NotifyEventPdpRequest(IEvent event) {
		_event = event;
	}

	@Override
	public IResponse process(PdpProcessor processor) {
		return processor.notifyEventSync(_event);
	}

}
