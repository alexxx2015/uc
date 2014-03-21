package de.tum.in.i22.uc.cm.in;

import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.requests.Request;

class RequestWrapper<T extends Request> {
	private final IForwarder _forwarder;
	private final T _request;

	public RequestWrapper(T request, IForwarder forwarder) {
		_forwarder = forwarder;
		_request = request;
	}

	public IForwarder getForwarder() {
		return _forwarder;
	}

	public T getRequest() {
		return _request;
	}
}