package de.tum.in.i22.uc.cm.processing;

import java.util.Collection;

public abstract class Request<R, P extends Processor> {
	private R _response;

	private boolean _responseReady = false;

	/**
	 * Sets the response. Will throw a ClassCastException if specified response
	 * is not of type R.
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setResponse(Object response) {
		_response = (R) response;
		_responseReady = true;
	}

	public R getResponse() {
		return _response;
	}

	public boolean responseReady() {
		return _responseReady;
	}

	public abstract R process(P processor);

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

