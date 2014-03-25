package de.tum.in.i22.uc.cm.in;

import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.requests.Request;

public class RequestWrapper<R extends Request> {
	private final IForwarder _forwarder;
	private final R _request;

	public RequestWrapper(R request, IForwarder forwarder) {
		_forwarder = forwarder;
		_request = request;
	}

	public IForwarder getForwarder() {
		return _forwarder;
	}

	public R getRequest() {
		return _request;
	}
}