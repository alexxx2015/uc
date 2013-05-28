package de.tum.in.i22.pdp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;

public class EventHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static EventHandler _instance = null;
	private BlockingQueue<EventHandlerWrapper> _eventQueue = null;
	
	public static EventHandler getInstance() {
		if (_instance == null) {
			_instance = new EventHandler();
		}
		return _instance;
	}
	
	private EventHandler() {
		// maximum size of the queue 100
		_eventQueue = new ArrayBlockingQueue<EventHandlerWrapper>(100, true);
	}
	
	public void addEvent(IEvent event, IResponder responder) throws InterruptedException {
		// add event to the tail of the queue
		// put method blocks until the space in the queue is available
		
		EventHandlerWrapper obj = new EventHandlerWrapper(event, responder);
		_logger.debug("Add " + obj + " pair to the queue.");
		_eventQueue.put(obj);
	}

	public void run() {
		_logger.debug("Run method entered.");
		while (!Thread.interrupted()) {
			EventHandlerWrapper obj = null;
			try {
				obj = _eventQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}
						
			EStatus status = processEvent(obj.getEvent());
			
			IResponder responder = obj.getResponder();
			responder.forwardResponse(status);
		}
		
		// the thread is interrupted, stop processing the events
	}
	
	private synchronized EStatus processEvent(IEvent event) {
		//FIXME implement method		
		return EStatus.ALLOW;
	}

	private class EventHandlerWrapper {
		private IEvent _event;
		private IResponder _responder;
		public EventHandlerWrapper(IEvent event, IResponder responder) {
			super();
			_event = event;
			_responder = responder;
		}
		
		public IEvent getEvent() {
			return _event;
		}
		
		public IResponder getResponder() {
			return _responder;
		}

		@Override
		public String toString() {
			return "EventHandlerWrapper [_event=" + _event + ", _responder="
					+ _responder + "]";
		}
		
	}
}
