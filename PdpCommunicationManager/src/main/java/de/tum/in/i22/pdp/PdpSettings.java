package de.tum.in.i22.pdp;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.SettingsLoader;

/**
 *
 * @author Stoimenov, Florian Kelbert
 * Settings are read from the specified properties file.
 * If no file is specified, file "pdp.properties" is used.
 *
 * Only the first invocation of getInstance(...) might carry the filename parameter.
 *
 */
public class PdpSettings {

	private static Logger _logger = Logger.getLogger(PdpSettings.class);

	private static PdpSettings _instance;

	private static final String DEFAULT_PROPERTIES_FILE_NAME = "pdp.properties";

	private final String propertiesFilename;

	// default values will be overridden with the values from the properties file
	private int _pepGPBListenerPortNum = 10001;
	private int _pepThriftListenerPortNum=20001;
	private int _pmpListenerPortNum = 10002;
	private int _pipListenerPortNum = 10003;

	private String _pipAddress;
	private int _pipPortNum;
	private int _queueSize = 100;

	private String _pepPipeIn = "/tmp/pep2pdp";
	private String _pepPipeOut = "/tmp/pdp2pep";

	private boolean _pmpListenerEnabled = true;
	private boolean _pepGPBListenerEnabled = true;
	private boolean _pepThriftListenerEnabled = true;
	private boolean _pipListenerEnabled = true;
	private boolean _pepPipeListenerEnabled = true;

	private PdpSettings() {
		this(DEFAULT_PROPERTIES_FILE_NAME);
	}

	private PdpSettings(String propertiesFilename) {
		this.propertiesFilename = propertiesFilename;
	}

	public static PdpSettings getInstance() {
		return (_instance != null)
			? _instance
			: getInstance(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public static PdpSettings getInstance(String propertiesFilename) {
		if (_instance != null) {
			throw new IllegalStateException("PDP properties have already been initialized and loaded. "
					+ "Only the first invocation of " + PdpSettings.class + ".getInstance() might be invoked with a parameter.");
		}

		_instance = new PdpSettings(propertiesFilename);

		return _instance;
	}

	public void loadProperties() throws IOException {
		Properties props = SettingsLoader.loadProperties(propertiesFilename);

		loadPepGpbListenerProperties(props);
		loadPepThriftListenerProperties(props);
		loadPepPipeListenerProperties(props);
		loadPmpListenerProperties(props);
		loadPipListenerProperties(props);

		try {
			_pipAddress = props.getProperty("pip_address");
		} catch (Exception e) {
			_logger.fatal("Cannot read pip address.", e);
			_logger.debug("Killing the app.");
			System.exit(1);
		}

		try {
			_pipPortNum = Integer.valueOf(props.getProperty("pip_port_num"));
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


	private void loadPepPipeListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_pipe_listener_enabled");
			if (s != null) {
				_pepPipeListenerEnabled = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep pipe listener.", e);
			_logger.info("Enabling pep pipe listener by default: " + _pepPipeListenerEnabled);
		}

		if (_pepPipeListenerEnabled) {
			String tmpInPipe = (String) props.get("pep_pipe_in");
			String tmpOutPipe = (String) props.get("pep_pipe_out");

			if (tmpInPipe == null || tmpOutPipe == null) {
				_logger.info("Cannot read pipe information.");
				_logger.info("Default pipes: " + _pepPipeIn + " and " + _pepPipeOut);
			}
			else {
				_pepPipeIn = tmpInPipe;
				_pepPipeOut = tmpOutPipe;
			}
		}
	}

	private void loadPipListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pip_listener_enabled");
			if (s != null) {
				_pipListenerEnabled = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pip listener.", e);
			_logger.info("Enabling pip listener by default: " + _pipListenerEnabled);
		}

		if (_pipListenerEnabled) {
			try {
				_pipListenerPortNum = Integer.valueOf(props.getProperty("pip_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read pip listener port number.", e);
				_logger.info("Default port of pip listener: " + _pipListenerPortNum);
			}
		}
	}

	private void loadPepThriftListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_Thrift_listener_enabled");
			if (s != null) {
				_pepThriftListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep Thrift listener.", e);
			_logger.info("Enabling pep Thrift listener by default: " + _pepThriftListenerEnabled);
		}

		if (_pepThriftListenerEnabled) {
			try {
				_pepThriftListenerPortNum = Integer.valueOf((String)props.get("pep_Thrift_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read Thrift pep listener port number.", e);
				_logger.info("Default port of Thrift pep listener: " + _pepGPBListenerPortNum);
			}
		}
	}

	private void loadPepGpbListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pep_GPB_listener_enabled");
			if (s != null) {
				_pepGPBListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pep GPB listener.", e);
			_logger.info("Enabling pep GPB listener by default: " + _pepGPBListenerEnabled);
		}

		if (_pepGPBListenerEnabled) {
			try {
				_pepGPBListenerPortNum = Integer.valueOf((String)props.get("pep_GPB_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read GPB pep listener port number.", e);
				_logger.info("Default port of GPB pep listener: " + _pepGPBListenerPortNum);
			}
		}
	}

	private void loadPmpListenerProperties(Properties props) {
		try {
			String s = props.getProperty("pmp_listener_enabled");
			if (s != null) {
				_pmpListenerEnabled  = Boolean.valueOf(s);
			}
		} catch (Exception e) {
			_logger.warn("Cannot read whether to enable pmp listener.", e);
			_logger.info("Enabling pmp listener by default: " + _pmpListenerEnabled);
		}

		if (_pmpListenerEnabled) {
			try {
				_pmpListenerPortNum = Integer.valueOf(props.getProperty("pmp_listener_port_num"));
			} catch (Exception e) {
				_logger.warn("Cannot read pmp listener port number.", e);
				_logger.info("Default port of pmp listener: " + _pmpListenerPortNum);
			}
		}
	}

	public String getPropertiesFileName() {
		return propertiesFilename;
	}

	public int getPepGPBListenerPortNum() {
		return _pepGPBListenerPortNum;
	}

	public int getPepThriftListenerPortNum() {
		return _pepThriftListenerPortNum;
	}

	public String getPepPipeIn() {
		return _pepPipeIn;
	}

	public String getPepPipeOut() {
		return _pepPipeOut;
	}

	public int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}

	public int getPipListenerPortNum() {
		return _pipListenerPortNum;
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

	public boolean isPmpListenerEnabled() {
		return _pmpListenerEnabled;
	}

	public boolean isPepGPBListenerEnabled() {
		return _pepGPBListenerEnabled;
	}

	public boolean isPepThriftListenerEnabled() {
		return _pepThriftListenerEnabled;
	}

	public boolean isPipListenerEnabled() {
		return _pipListenerEnabled;
	}

	public boolean isPepPipeListenerEnabled() {
		return _pepPipeListenerEnabled;
	}

	public void setPepGPBListenerPortNum(int pepGPBListenerPortNum) {
		_pepGPBListenerPortNum = pepGPBListenerPortNum;
	}

	public void setPepThriftListenerPortNum(int pepThriftListenerPortNum) {
		_pepThriftListenerPortNum = pepThriftListenerPortNum;
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

