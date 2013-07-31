package de.tum.in.i22.pdp.cm.in;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pdp.cm.CommunicationHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpRequest;
import de.tum.in.i22.pdp.datatypes.IEvent;

public class RequestHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static RequestHandler _instance = null;
	private BlockingQueue<RequestWrapper> _requestQueue = null;
	
	private CommunicationHandler communicationHandler = CommunicationHandler.getInstance();
	
	public static RequestHandler getInstance() {
		if (_instance == null) {
			_instance = new RequestHandler();
		}
		return _instance;
	}
	
	private RequestHandler() {
		_requestQueue = 
				new ArrayBlockingQueue<RequestWrapper>(PdpSettings.getQueueSize(), true);
	}
	
	public void addEvent(IEvent event, IForwarder forwarder) throws InterruptedException {				
		// add event to the tail of the queue
		// put method blocks until the space in the queue is available
		RequestWrapper obj = new PepRequestWrapper(forwarder, event);
		_logger.debug("Add " + obj + " pair to the queue.");
		_requestQueue.put(obj);
	}
	
	public void addPmpRequest(PmpRequest request, IForwarder forwarder)
			throws InterruptedException {
		// add pmpRequest to the tail of the queue
		// put method blocks until the space in the queue is available
		RequestWrapper obj = new PmpRequestWrapper(forwarder, request);
		_logger.debug("Add " + obj + " pair to the queue.");
		_requestQueue.put(obj);
	}

	public void run() {
		_logger.debug("Request handler run method");
		while (!Thread.interrupted()) {
			RequestWrapper request = null;
			try {
				request = _requestQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}
			
			Object response = null;
			if (request instanceof PepRequestWrapper) {
				IEvent event = ((PepRequestWrapper)request).getEvent();
//				if (event instanceof IActualEvent) {
				// FIXME if ActualEvent invoke PIP
				Exception e;
				response = communicationHandler.notifyEvent(event);
				
			} else if (request instanceof PmpRequestWrapper) {
				PmpRequest pmpRequest = ((PmpRequestWrapper)request).getPmpRequest();
				response = processPmpRequest(pmpRequest);
			} else {
				throw new RuntimeException("Queue element " + request + " must be either event or PmpRequest!");
			}
			
			IForwarder forwarder = request.getForwarder();
			forwarder.forwardResponse(response);
		}
		
		// the thread is interrupted, stop processing the events
	}
	
	private Object processPmpRequest(PmpRequest pmpRequest) {
		_logger.debug("Process event " + pmpRequest);
		Object result = null;
		switch (pmpRequest.getMethod()) {
		case DEPLOY_MECHANISM: 
			result = communicationHandler.deployMechanism(pmpRequest.getMechanism());
			break;
		case EXPORT_MECHANISM: 
			result = communicationHandler.exportMechanism(pmpRequest.getStringParameter());
			break;
		case REVOKE_MECHANISM:  
			result = communicationHandler.revokeMechanism(pmpRequest.getStringParameter());
			break;
		default: 
			throw new RuntimeException("Method " + pmpRequest.getMethod() + " is not supported!");
		}
		return result;
	}

	private class RequestWrapper {
		// Forwarder is the thread that will send back the response
		private IForwarder _forwarder;

		public RequestWrapper(IForwarder forwarder) {
			super();
			_forwarder = forwarder;
		}
		
		public IForwarder getForwarder() {
			return _forwarder;
		}
		
	}
	
	private class PmpRequestWrapper extends RequestWrapper {
		private PmpRequest _pmpRequest;
		
		public PmpRequestWrapper(IForwarder forwarder, PmpRequest pmpRequest) {
			super(forwarder);
			_pmpRequest = pmpRequest;
		}

		@Override
		public String toString() {
			return "PmpRequestWrapper [_pmpRequest=" + _pmpRequest + "]";
		}
		
		public PmpRequest getPmpRequest() {
			return _pmpRequest;
		}
	}
	
	private class PepRequestWrapper extends RequestWrapper {
		private IEvent _event;

		public PepRequestWrapper(IForwarder forwarder, IEvent event) {
			super(forwarder);
			_event = event;
		}

		@Override
		public String toString() {
			return "PepRequestWrapper [_event=" + _event + "]";
		}
		
		public IEvent getEvent() {
			return _event;
		}
		
	}
}
