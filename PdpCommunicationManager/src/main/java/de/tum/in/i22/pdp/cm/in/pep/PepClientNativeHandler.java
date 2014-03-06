package de.tum.in.i22.pdp.cm.in.pep;

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

	public static void notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) throws InterruptedException {
		System.out.println("strt: " + System.nanoTime());
		if (name == null || paramKeys == null || paramValues == null
				|| paramKeys.length != paramValues.length || name.isEmpty()) {
			// TODO rather send an immediate response
			return;
		}

		RequestHandler requestHandler = RequestHandler.getInstance();

		Map<String,String> params = new HashMap<String,String>();
		for (int i = 0; i < paramKeys.length; i++) {
			params.put(paramKeys[i], paramValues[i]);
		}

		// TODO: is actual?! -> Y/N
		IEvent event = new EventBasic(name, params, isActual);
		requestHandler.addEvent(event, new IForwarder() {

			@Override
			public void forwardResponse(Object response) {
				// TODO Implement callback
			}
		});

		System.out.println(" end: " + System.nanoTime());
	}
}
