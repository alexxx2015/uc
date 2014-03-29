package de.tum.in.i22.uc.cm.requests;

import java.util.Collection;

public abstract class Request<R> {
	private R _response;

	private boolean _responseReady = false;

	public void setResponse(R response) {
		_response = response;
		_responseReady = true;
	}

	public R getResponse() {
		return _response;
	}

	public boolean responseReady() {
		return _responseReady;
	}

	protected boolean paramsPresent(Object ... params) {
		boolean result = true;

		for (int i = 0; i < params.length && result; i++) {
			result = result && params[i] != null;
			if (params[i] instanceof Collection<?>) {
				result = result && ((Collection<?>) params[i]).size() > 0;
			}
		}

		return result;
	}
}

