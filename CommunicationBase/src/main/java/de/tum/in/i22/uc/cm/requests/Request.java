package de.tum.in.i22.uc.cm.requests;

import java.util.Collection;

public abstract class Request<R> {
	private Object _response;

	private boolean _responseReady = false;

	private final Class<R> _responseClass;

	protected Request(Class<R> responseClass) {
		_responseClass = responseClass;
	}

	public void setResponse(Object response) {
		_response = response;
		_responseReady = true;
	}

	public R getResponse() {
		return _responseClass.cast(_response);
	}

	public boolean responseReady() {
		return _responseReady;
	}

	public Class<?> getResponseClass() {
		return _responseClass;
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

