package de.tum.in.i22.pip;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.SettingsLoader;

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
	
	private static final String PROPERTIES_FILE_NAME = "pip.properties";
	
	// default values will be overridden with the values from the properties file
	private static int _pdpListenerPortNum = 60003;
	
	// tis class has only static methods, there is no need for objects
	private PipSettings() {}
	
	public static void loadProperties() throws IOException {
		Properties props = SettingsLoader.loadProperties(PROPERTIES_FILE_NAME);
		
		try {
			_pdpListenerPortNum = Integer.valueOf((String)props.get("pdp_listener_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read pdp listener port number.", e);
			_logger.info("Default port of pdp listener: " + _pdpListenerPortNum);
		}
	}
	
	public static int getPdpListenerPortNum() {
		return _pdpListenerPortNum;
	}
}
