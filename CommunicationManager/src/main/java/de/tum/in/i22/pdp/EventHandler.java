package de.tum.in.i22.pdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class EventHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static EventHandler _instance = null;
	private BlockingQueue<EventHandlerWrapper> _eventQueue = null;
	
	private static IMessageFactory _factory = MessageFactory.getInstance();
	
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
	
	public void addEvent(IEvent event, IForwarder responder) throws InterruptedException {
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
						
			IResponse response = processEvent(obj.getEvent());
			
			IForwarder responder = obj.getResponder();
			responder.forwardResponse(response);
		}
		
		// the thread is interrupted, stop processing the events
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

	private class EventHandlerWrapper {
		private IEvent _event;
		private IForwarder _responder;
		public EventHandlerWrapper(IEvent event, IForwarder responder) {
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
