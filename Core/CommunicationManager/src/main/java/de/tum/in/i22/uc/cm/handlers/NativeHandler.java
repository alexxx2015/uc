package de.tum.in.i22.uc.cm.handlers;

import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class NativeHandler {
	public static Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) throws InterruptedException {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		Object response = null;

		if (event != null) {
			if (isActual) {
				RequestHandler.getInstance().notifyEventAsync(event);
			}
			else {
				response = RequestHandler.getInstance().notifyEventSync(event);
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

