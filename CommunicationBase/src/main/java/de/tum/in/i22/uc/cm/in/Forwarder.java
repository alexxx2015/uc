package de.tum.in.i22.uc.cm.in;

import de.tum.in.i22.uc.cm.requests.Request;


public interface Forwarder {
	public void forwardResponse(Request request, Object response);
}
