package de.tum.in.i22.uc.cm.server;

public interface IRequestHandler {
	public void addRequest(Request<?,?> request, IForwarder forwarder);
}
