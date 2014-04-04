package de.tum.in.i22.uc.thrift.server;

import de.tum.in.i22.uc.cm.server.IForwarder;
import de.tum.in.i22.uc.cm.server.Request;

/**
 * A generic Thrift server handler, implementing functionalities
 * to be used by concrete subclasses.
 *
 * @author Florian Kelbert
 *
 */
abstract class ThriftServerHandler implements IForwarder {
	/**
	 * Waits for the specified request to be processed.
	 * Once the corresponding response is ready, execution
	 * continues and the request's response is returned.
	 *
	 * @param request the request for whose processing/response is waited for.
	 * @return the response corresponding to the request.
	 */
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
