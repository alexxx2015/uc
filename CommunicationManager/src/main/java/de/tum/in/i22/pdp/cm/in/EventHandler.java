package de.tum.in.i22.pdp.cm.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.pmp.PmpRequest;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class EventHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static EventHandler _instance = null;
	private BlockingQueue<RequestWrapper> _eventQueue = null;
	
	private boolean _pause = false;
	private Object _pauseLock = new Object();
	
	private static IMessageFactory _factory = MessageFactory.getInstance();
	
	public static EventHandler getInstance() {
		if (_instance == null) {
			_instance = new EventHandler();
		}
		return _instance;
	}
	
	private EventHandler() {
		// maximum size of the queue 100
		_eventQueue = new ArrayBlockingQueue<RequestWrapper>(100, true);
	}
	
	/**
	 * Causes the thread to stop processing the events in the queue.
	 */
	public void pause() {
		_logger.info("Pause Event Handler");
		_pause = true;
	}
	
	/**
	 * Resumes 
	 */
	public void resume() {
		_logger.info("Resume Event Handler");
		synchronized (_pauseLock) {
			_pause = false;
			_pauseLock.notifyAll();
		}
	}
	
	public void addEvent(IEvent event, IForwarder responder) throws InterruptedException {
		// add event to the tail of the queue
		// put method blocks until the space in the queue is available
		
		RequestWrapper obj = new RequestWrapper(event, responder);
		_logger.debug("Add " + obj + " pair to the queue.");
		_eventQueue.put(obj);
	}

	public void run() {
		_logger.debug("Run method entered.");
		while (!Thread.interrupted()) {
			RequestWrapper obj = null;
			try {
				obj = _eventQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}
						
			IResponse response = processEvent(obj.getEvent());
			
			IForwarder responder = obj.getResponder();
			responder.forwardResponse(response);
			
			// now check if there is a request to pause the thread
			checkForPaused();
		}
		
		// the thread is interrupted, stop processing the events
	}
	
	private void checkForPaused() {
		synchronized(_pauseLock) {
			while (_pause) {
				try {
					_logger.info("Invoke wait");
					_pauseLock.wait();
					_logger.info("After wait");
				} catch (InterruptedException e) {
					_logger.error("Event handler interrupted.", e);
					return;
				}
			}
		}
	}

	private synchronized IResponse processEvent(IEvent event) {
		_logger.debug("Process event " + event.getName());
		EStatus status = EStatus.ALLOW;
		
		List<IEvent> executeActions = new ArrayList<IEvent>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("key1", "val1");
		map1.put("key2", "val2");
		IEvent action1 = _factory.createEvent("event1", map1);
		IEvent action2 = _factory.createEvent("event2", map1);
		executeActions.add(action1);
		executeActions.add(action2);
		
		IEvent modifiedEvent = _factory.createEvent("eventModified", map1);
		
		ResponseBasic response = new ResponseBasic(status, executeActions, modifiedEvent);
		
		return response;
	}

	private class RequestWrapper {
		private IEvent _event;
		private IForwarder _responder;
		public RequestWrapper(IEvent event, IForwarder responder) {
			super();
			_event = event;
			_responder = responder;
		}
		
		public IEvent getEvent() {
			return _event;
		}
		
		public IForwarder getResponder() {
			return _responder;
		}

		@Override
		public String toString() {
			return "EventHandlerWrapper [_event=" + _event + ", _responder="
					+ _responder + "]";
		}
		
	}
}
