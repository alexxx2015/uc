package de.tum.in.i22.pdp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Stoimenov
 * Settings are read from properties file named "pdp.properties".
 * This class has only static methods. There is no need of instances 
 * of the class.
 *
 */
public class PdpSettings {
	
	private static Logger _logger = Logger.getLogger(PdpSettings.class);
	
	private static final String PROPERTIES_FILE_NAME = "pdp.properties";
	
	// default values are overridden with the values from the properties file
	private static int _pepListenerPortNum = 60001;
	private static int _pmpListenerPortNum = 60002;
	private static int _maxPmpToPdpMessageSize = 1024; // in B
	private static int _maxPepToPdpMessageSize = 1024; // in B
	private static int _queueSize = 100;
	
	// the class is static, no need for objects
	private PdpSettings() {}
	
	static {
		_logger.debug("Loading properties file: " + PROPERTIES_FILE_NAME);

		InputStream is = null;
		try {
			_logger.debug("Searching properties file " + PROPERTIES_FILE_NAME
					+ " in jar parent directory ...");
			File file = new File(new File("."), PROPERTIES_FILE_NAME);
			
			if (file.exists()) {
				_logger.debug("File found. Loading file ...");

				is = new FileInputStream(file);

			} else {
				_logger.debug("File not found. Loading file from resources ...");
				is = PdpSettings.class.getClassLoader().getResourceAsStream(
						PROPERTIES_FILE_NAME);
			}

			// load a properties file
			Properties props = new Properties();
			// load all the properties from this file
			props.load(is);
			_logger.debug("Properties file loaded.");
			
			readProperties(props);
			
		} catch (IOException ex) {
			_logger.error("Error occured while loading properties file.", ex);
		} finally {
			if (is != null) {
				// we have loaded the properties, so close the file handler
				try {
					is.close();
				} catch (IOException e) {
					_logger.error("Failed to close input stream for properties file.", e);
				}
			}
		}
	}

	private static void readProperties(Properties props) {
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

