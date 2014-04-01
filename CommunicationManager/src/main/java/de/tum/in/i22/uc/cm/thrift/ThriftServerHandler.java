package de.tum.in.i22.uc.cm.thrift;

import de.tum.in.i22.uc.cm.in.Forwarder;
import de.tum.in.i22.uc.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class ThriftServerHandler implements Forwarder {

	protected final RequestHandler _requestHandler;

	public ThriftServerHandler() {
		_requestHandler = RequestHandler.getInstance();
	}

	protected <T> T waitForResponse(Request<T,?> request) {
		T result = null;

		synchronized (this) {
			while (!request.responseReady()) {
				try {
					wait();
				} catch (InterruptedException e) {	}
			}
			result = request.getResponse();
		}

		return result;
	}

	@Override
	public void forwardResponse(Request<?,?> request, Object response) {
		synchronized (this) {
			request.setResponse(response);
			notifyAll();
		}
	}

}
