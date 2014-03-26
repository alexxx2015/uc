package de.tum.in.i22.uc.cm.in;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.requests.EPdpRequestType;
import de.tum.in.i22.uc.cm.requests.PdpRequest;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class NativeHandler {
	private static final Map<IEvent, Object> responses = Collections.synchronizedMap(new HashMap<IEvent, Object>());

	public static void addResponse(IEvent event, Object response) {
		synchronized (responses) {
			responses.put(event, response);
			responses.notifyAll();
		}
	}

	public static Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) throws InterruptedException {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		PdpRequest req = new PdpRequest(EPdpRequestType.NOTIFY_EVENT, event);
		Object response = null;

		if (event != null) {
			if (isActual) {
				RequestHandler.addRequest(req, new IForwarder() {
					@Override
					public void forwardResponse(Object response) { }
				});
			}
			else {
				synchronized(responses) {
					RequestHandler.addRequest(req, new NativeForwarder(event));
				}

				synchronized (responses) {
					response = responses.remove(event);
					while (response == null) {
						responses.wait();
						response = responses.remove(event);
					}
				}
			}
		}

		return response;
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

class NativeForwarder implements IForwarder {
	private final IEvent _event;

	public NativeForwarder(IEvent ev) {
		_event = ev;
	}

	@Override
	public void forwardResponse(Object response) {
		NativeHandler.addResponse(_event, response);
	}
}

