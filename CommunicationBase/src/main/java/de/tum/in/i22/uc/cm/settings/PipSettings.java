package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.SettingsLoader;

/**
 *
 * @author Stoimenov
 *
 * Settings are loaded from properties file named "pip.properties".
 * This class has only static methods because there is no need
 * for instances of the class.
 *
 */
public class PipSettings {

	private static final Logger _logger = Logger.getLogger(PipSettings.class);
	private static final PipSettings _instance = new PipSettings();

	private static final String PROPERTIES_FILE_NAME = "pip.properties";

	// default values will be overridden with the values from the properties file
	private int _pdpListenerPortNum = 60003;

	private int _pmpListenerPortNum = 60005;

	private int _pipRemotePortNum = 10003;

	private PipSettings() {}

	public static PipSettings getInstance() {
		return _instance;
	}

	public void loadProperties() throws IOException {
		Properties props = SettingsLoader.loadProperties(PROPERTIES_FILE_NAME);

		try {
			_pdpListenerPortNum = Integer.valueOf((String)props.get("pdp_listener_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read pdp listener port number.", e);
			_logger.info("Default port of pdp listener: " + _pdpListenerPortNum);
		}

		try {
			_pmpListenerPortNum = Integer.valueOf((String)props.get("pmp_listener_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read pmp listener port number.", e);
			_logger.info("Default port of pmp listener: " + _pdpListenerPortNum);
		}

		try {
			_pipRemotePortNum = Integer.valueOf((String)props.get("pip_remote_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read remote pip port number.", e);
			_logger.info("Default port of remote pip: " + _pipRemotePortNum);
		}



	}

	public int getPdpListenerPortNum() {
		return _pdpListenerPortNum;
	}

	public int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}

	public int getPipRemotePortNum() {
		return _pipRemotePortNum;
	}

	public void setPdpListenerPortNum(int pdpListenerPortNum) {
		_pdpListenerPortNum = pdpListenerPortNum;
	}

	public void setPmpListenerPortNum(int pmpListenerPortNum) {
		_pmpListenerPortNum = pmpListenerPortNum;
	}
}
