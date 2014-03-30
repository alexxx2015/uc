package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.pdp.PdpProcessor;
import de.tum.in.i22.uc.pdp.PdpRequest;

public class NotifyEventPdpRequest extends PdpRequest<IResponse> {

	public NotifyEventPdpRequest(IEvent event) {
		super(event);
	}

	@Override
	public IResponse process(PdpProcessor processor) {
		IResponse res = processor.notifyEvent(_event);
		processor.getPip().notifyActualEvent(_event);
		return res;
	}

}
