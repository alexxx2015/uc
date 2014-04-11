package de.tum.in.i22.uc.cm.processing;


/**
 * Forwarder interface, that will be invoked whenever a
 * {@link Request} has been processed and the corresponding
 * response is ready.
 *
 * @author Florian Kelbert
 *
 */
public interface IForwarder {
	/**
	 * Invoked when the specified request has been processed.
	 * The specified response is this request's response.
	 *
	 * @param request the request
	 * @param response the request's response
	 */
	public void forwardResponse(Request<?,?> request, Object response);
}
