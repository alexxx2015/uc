package de.tum.in.i22.uc.pdp;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class PdpRequest<R> extends Request<R,PdpProcessor> {
	protected IPxpSpec _pxp;
	protected IEvent _event;

	public PdpRequest(IPxpSpec pxp) {
		_pxp = pxp;
	}

	public PdpRequest(IEvent event) {
		_event = event;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_pxp", _pxp)
				.add("_event", _event)
				.toString();
	}

}
