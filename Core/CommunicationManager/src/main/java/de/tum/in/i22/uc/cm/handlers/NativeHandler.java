package de.tum.in.i22.uc.cm.handlers;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class NativeHandler {
	private final IRequestHandler _requestHandler;

	public NativeHandler(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	public Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		Object response = null;

		if (event != null) {
			if (isActual) {
				_requestHandler.notifyEventAsync(event);
			}
			else {
				response = _requestHandler.notifyEventSync(event);
			}
		}

		return response;
	}

	private IEvent assembleEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
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

