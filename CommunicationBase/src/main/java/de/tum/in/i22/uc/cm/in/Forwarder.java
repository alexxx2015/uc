package de.tum.in.i22.uc.cm.in;

import de.tum.in.i22.uc.cm.requests.Request;


public interface Forwarder {
	public <R extends Object> void forwardResponse(Request<R> request, R response);
}
