package de.tum.in.i22.pdp.cm.in;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.CommunicationHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpRequest;
import de.tum.in.i22.pdp.datatypes.IEvent;

public class RequestHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static RequestHandler _instance = null;
	private BlockingQueue<RequestWrapper> _requestQueue = null;
	
	private CommunicationHandler communicationHandler = CommunicationHandler.getInstance();
	
	//TODO currently not used, but consider using this approach
	private boolean _pause = false;
	private Object _pauseLock = new Object();
	
	public static RequestHandler getInstance() {
		if (_instance == null) {
			_instance = new RequestHandler();
		}
		return _instance;
	}
	
	private RequestHandler() {
		// maximum size of the queue 100
		_requestQueue = new ArrayBlockingQueue<RequestWrapper>(100, true);
	}
	
	/**
	 * Causes the thread to stop processing the events in the queue.
	 * It blocks until it is sure the handler has been stopped.
	 */
	public void pause() {
		_logger.info("Pause Event Handler");
		synchronized(_pauseLock) {
			_pause = true;
			while (_pause) {
				try {
					_pauseLock.wait();
				} catch (InterruptedException e) {
					
				}
			}
		}
	}
	
	/**
	 * Resumes 
	 */
	public void resume() {
		_logger.info("Resume Event Handler");
		synchronized(_pauseLock) {
			_pause = false;
			_pauseLock.notifyAll();
		}
	}
	
	public void addEvent(IEvent event, IForwarder responder) throws InterruptedException {				
		// add event to the tail of the queue
		// put method blocks until the space in the queue is available
		RequestWrapper obj = new RequestWrapper(event, responder);
		_logger.debug("Add " + obj + " pair to the queue.");
		_requestQueue.put(obj);
	}
	
	public void addPmpRequest(PmpRequest request, IForwarder responder)
			throws InterruptedException {
		// add pmpRequest to the tail of the queue
		// put method blocks until the space in the queue is available
		RequestWrapper obj = new RequestWrapper(request, responder);
		_logger.debug("Add " + obj + " pair to the queue.");
		_requestQueue.put(obj);
	}

	public void run() {
		_logger.debug("Run method entered.");
		while (!Thread.interrupted()) {
			RequestWrapper obj = null;
			try {
				obj = _requestQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}
			
			Object response = null;
			if (obj.getEvent() != null)
				response = communicationHandler.notifyEvent(obj.getEvent());
			else if (obj.getPmpRequest() != null) {
				response = processPmpRequest(obj.getPmpRequest());
			} else {
				throw new RuntimeException("Queue element " + obj + " must be either event or PmpRequest!");
			}
			
			IForwarder responder = obj.getResponder();
			responder.forwardResponse(response);
		}
		
		// the thread is interrupted, stop processing the events
	}
	
//	private void checkIfPausedAndWait() {
//		synchronized(_pauseLock) {
//			while (_pause) {
//				try {
//					_logger.info("Invoke wait");
//					_pauseLock.wait();
//					_logger.info("After wait");
//				} catch (InterruptedException e) {
//					_logger.error("Event handler interrupted.", e);
//					return;
//				}
//			}
//		}
//	}
	
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
		private IEvent _event;
		private PmpRequest _pmpRequest;
		private IForwarder _forwarder;
		
		public RequestWrapper(IEvent event, IForwarder forwarder) {
			super();
			_pmpRequest = null;
			_event = event;
			_forwarder = forwarder;
		}
		
		public RequestWrapper(PmpRequest pmpRequest, IForwarder forwarder) {
			super();
			_event = null;
			_pmpRequest = pmpRequest;
			_forwarder = forwarder;
		}
		
		public IEvent getEvent() {
			return _event;
		}
		
		public PmpRequest getPmpRequest() {
			return _pmpRequest;
		}
		
		public IForwarder getResponder() {
			return _forwarder;
		}

		@Override
		public String toString() {
			return "RequestWrapper [_event=" + _event + ", _pmpRequest="
					+ _pmpRequest + ", _forwarder=" + _forwarder + "]";
		}
		
	}
}
