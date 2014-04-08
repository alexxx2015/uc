package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.server.PdpProcessor;


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
//
//		/**
//		 * (1) If the event is actual, we update the PIP in any case
//		 *
//		 * (2) If the event is *not* actual
//		 * 		AND if the event was allowed by the PDP
//		 * 		AND if for this event allowance implies that the event is to be considered as actual event,
//		 * 	   then we create the corresponding actual event and signal it to both the PIP and the PDP
//		 *     as actual event.
//		 */
//
//		if (_event.isActual()) {
//			processor.getPip().update(_event);
//		}
//		else if (res.getAuthorizationAction().isStatus(EStatus.ALLOW) && _event.allowImpliesActual()) {
//			IEvent ev2 = new EventBasic(_event.getName(), _event.getParameters(), true);
//			// TODO: Check whether this order is correct. Enrico?
//			processor.getPip().update(ev2);
//			processor.notifyEventAsync(ev2);
//		}
//
//		return res;
	}

}
