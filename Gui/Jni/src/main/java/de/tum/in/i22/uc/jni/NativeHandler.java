package de.tum.in.i22.uc.jni;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

/**
 * This class will be used via JNI to dispatch events.
 * @author Florian Kelbert
 *
 */
public class NativeHandler extends Controller {
	private static Logger _logger = LoggerFactory.getLogger(NativeHandler.class);

	private static NativeHandler _instance;

	private IPep2Pdp _pep2pdp;

	private NativeHandler(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		_instance = new NativeHandler(args);

		if (Settings.getInstance().isPdpListenerEnabled()) {
			if (_instance.start()) {
				_instance.set(_instance);
			} else {
				_logger.error("Unable to start UC infrastructure. Exiting.");
				System.exit(1);
			}
		}
		else {
			Pep2PdpClient pep2pdp = new ThriftClientFactory().createPep2PdpClient(Settings.getInstance().getPdpLocation());
			try {
				_logger.info("Connecting to {}.", Settings.getInstance().getPdpLocation());
				pep2pdp.connect();
			} catch (IOException e) {
				_logger.error("Unable to connect to {}.", Settings.getInstance().getPdpLocation());
				System.exit(1);
			}
			_instance.set(pep2pdp);
		}

		// lock forever
		lock();
	}

	private void set(IPep2Pdp pep2pdp) {
			_pep2pdp = pep2pdp;
	}

	public Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		Object response = null;

		if (event != null) {
			if (isActual) {
				_pep2pdp.notifyEventAsync(event);
			}
			else {
				response = _pep2pdp.notifyEventSync(event);
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
		if (Settings.getInstance().isPdpListenerEnabled()) {
			return super.isStarted();
		}

		return true;
	}
}

