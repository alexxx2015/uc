package de.tum.in.i22.pdp;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Stoimenov
 * Settings are read from properties file named "pdp.properties".
 * This class has only static methods. There is no need for instances 
 * of the class.
 *
 */
public class PdpSettings {
	
	private static Logger _logger = Logger.getLogger(PdpSettings.class);
	
	private static final String PROPERTIES_FILE_NAME = "pdp.properties";
	
	// default values wil be overridden with the values from the properties file
	private static int _pepListenerPortNum = 60001;
	private static int _pmpListenerPortNum = 60002;
	private static int _maxPmpToPdpMessageSize = 1024; // in B
	private static int _maxPepToPdpMessageSize = 1024; // in B
	private static int _queueSize = 100;
	
	// the class is static, no need for objects
	private PdpSettings() {}

	public static void loadProperties() throws IOException {
		Properties props = SettingsLoader.loadProperties(PROPERTIES_FILE_NAME);
		
		try {
			_pepListenerPortNum = Integer.valueOf((String)props.get("pep_listener_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read pep listener port number.", e);
			_logger.info("Default port of pep listener: " + _pepListenerPortNum);
		}
		
		try {
			_pmpListenerPortNum = Integer.valueOf((String)props.getProperty("pmp_listener_port_num"));
		} catch (Exception e) {
			_logger.warn("Cannot read pmp listener port number.", e);
			_logger.info("Default port of pmp listener: " + _pmpListenerPortNum);
		}
		
		try {
			_maxPepToPdpMessageSize = Integer.valueOf((String)props.get("max_pep_to_pdp_message_size"));
		} catch (Exception e) {
			_logger.warn("Cannot read max pep to pdp message size.", e);
			_logger.info("Default max pep to pdp message size: " + _maxPepToPdpMessageSize);
		}
		
		try {
			_maxPmpToPdpMessageSize = Integer.valueOf((String)props.get("max_pmp_to_pdp_message_size"));
		} catch (Exception e) {
			_logger.warn("Cannot read max pmp to pdp message size.", e);
			_logger.info("Default max pmp to pdp message size: " + _maxPmpToPdpMessageSize);
		}
		
		try {
			_queueSize = Integer.valueOf((String)props.get("queue_size"));
		} catch (Exception e) {
			_logger.warn("Cannot read queue size.", e);
			_logger.info("Default queue size: " + _maxPmpToPdpMessageSize);
		}
	}

	public static Logger getLogger() {
		return _logger;
	}

	public static String getPropertiesFileName() {
		return PROPERTIES_FILE_NAME;
	}

	public static int getPepListenerPortNum() {
		return _pepListenerPortNum;
	}

	public static int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}

	public static int getMaxPmpToPdpMessageSize() {
		return _maxPmpToPdpMessageSize;
	}

	public static int getMaxPepToPdpMessageSize() {
		return _maxPepToPdpMessageSize;
	}
	
	public static int getQueueSize() {
		return _queueSize;
	}
	
}

