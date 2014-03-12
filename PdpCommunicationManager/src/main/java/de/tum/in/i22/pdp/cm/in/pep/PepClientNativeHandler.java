package de.tum.in.i22.pdp.cm.in.pep;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.in.IForwarder;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class PepClientNativeHandler {
	private static final Map<IEvent, Object> responses = Collections.synchronizedMap(new HashMap<IEvent, Object>());

	public static void addResponse(IEvent event, Object response) {
		synchronized (responses) {
			responses.put(event, response);
			responses.notifyAll();
		}
	}

	public static Object getResponse(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		if (event == null) {
			// TODO rather: respond
			return null;
		}

		Object response = null;

		synchronized (responses) {
			while (response == null) {
				try {
					responses.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return null;
				}
				response = responses.remove(event);
			}
		}

		return response;
	}

	public static void notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) throws InterruptedException {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		if (event == null) {
			// TODO rather: respond
			return;
		}

		RequestHandler requestHandler = RequestHandler.getInstance();

		if (isActual) {
			requestHandler.addEvent(event, new IForwarder() {
				@Override
				public void forwardResponse(Object response) { }
			});
		}
		else {
			requestHandler.addEvent(event, new PepClientNativeForwarder(event));
		}
	}

	private static IEvent assembleEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		if (name == null || paramKeys == null || paramValues == null
				|| paramKeys.length != paramValues.length || name.isEmpty()) {
			return null;
		}

		Map<String,String> params = new HashMap<String,String>();
		for (int i = 0; i < paramKeys.length; i++) {
			params.put(paramKeys[i], paramValues[i]);
		}

		return new EventBasic(name, params, isActual);
	}
}

class PepClientNativeForwarder implements IForwarder {
	private final IEvent _event;

	public PepClientNativeForwarder(IEvent ev) {
		_event = ev;
	}

	@Override
	public void forwardResponse(Object response) {
		PepClientNativeHandler.addResponse(_event, response);
	}
}