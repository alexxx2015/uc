package de.tum.in.i22.pdp;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.SettingsLoader;

/**
 * 
 * @author Stoimenov
 * Settings are read from properties file named "pdp.properties".
 * Singleton. 
 *
 */
public class PdpSettings {
	
	private static Logger _logger = Logger.getLogger(PdpSettings.class);
	
	private final static PdpSettings _instance = new PdpSettings();
	
	private final String PROPERTIES_FILE_NAME = "pdp.properties";
	
	// default values will be overridden with the values from the properties file
	private int _pepListenerPortNum = 60001;
	private int _pmpListenerPortNum = 60002;
	
	private String _pipAddress;
	private int _pipPortNum;
	private int _queueSize = 100;
	
	private PdpSettings() {}
	
	public static PdpSettings getInstance() {
		return _instance;
	}

	public void loadProperties() throws IOException {
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
			_pipAddress = (String)props.getProperty("pip_address");
		} catch (Exception e) {
			_logger.fatal("Cannot read pip address.", e);
			_logger.debug("Killing the app.");
			System.exit(1);
		}
		
		try {
			_pipPortNum = Integer.valueOf((String)props.getProperty("pip_port_num"));
		} catch (Exception e) {
			_logger.fatal("Cannot read pip port number.", e);
			_logger.debug("Killing the app.");
			System.exit(1);
		}
		
		try {
			_queueSize = Integer.valueOf((String)props.get("queue_size"));
		} catch (Exception e) {
			_logger.warn("Cannot read queue size.", e);
			_logger.info("Default queue size: " + _queueSize);
		}
	}


	public String getPropertiesFileName() {
		return PROPERTIES_FILE_NAME;
	}

	public int getPepListenerPortNum() {
		return _pepListenerPortNum;
	}

	public int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}
	
	public String getPipAddress() {
		return _pipAddress;
	}
	
	public int getPipPortNum() {
		return _pipPortNum;
	}

	public int getQueueSize() {
		return _queueSize;
	}

	public void setPepListenerPortNum(int pepListenerPortNum) {
		_pepListenerPortNum = pepListenerPortNum;
	}

	public void setPmpListenerPortNum(int pmpListenerPortNum) {
		_pmpListenerPortNum = pmpListenerPortNum;
	}

	public void setPipAddress(String pipAddress) {
		_pipAddress = pipAddress;
	}

	public void setPipPortNum(int pipPortNum) {
		_pipPortNum = pipPortNum;
	}

	public void setQueueSize(int queueSize) {
		_queueSize = queueSize;
	}
	
}

