package de.tum.in.i22.uc.jni;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class NativeHandler extends Controller {
	private static Logger _logger = LoggerFactory.getLogger(NativeHandler.class);

	private static NativeHandler _instance;

	public NativeHandler(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		_instance = new NativeHandler(args);
		if (_instance.start()) {
			// lock forever
			lock();
		} else {
			_logger.error("Unable to start UC infrastructure. Exiting.");
			System.exit(1);
		}
	}

	public Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		Object response = null;

		if (event != null) {
			if (isActual) {
				_instance.notifyEventAsync(event);
			}
			else {
				response = _instance.notifyEventSync(event);
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

	@Override
	public boolean isStarted() {
		return super.isStarted();
	}
}

