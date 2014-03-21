package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;


/**
 * @author Florian Kelbert
 *
 */
public class NotifyEventRequest extends Request  {
	private final IEvent _event;

	public NotifyEventRequest(IEvent event) {
		_event = event;
	}

	@Override
	public String toString() {
		return "NotifyEventRequest[_event=" + _event + "]";
	}

	public IEvent getEvent() {
		return _event;
	}
}