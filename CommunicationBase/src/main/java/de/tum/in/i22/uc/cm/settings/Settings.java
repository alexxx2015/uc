package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;

/**
 *
 * @author Stoimenov, Florian Kelbert
 * Settings are read from the specified properties file.
 * If no file is specified, file "pdp.properties" is used.
 *
 * Only the first invocation of getInstance(...) might carry the filename parameter.
 *
 */
public class Settings extends SettingsLoader {

	private static Logger _logger = LoggerFactory.getLogger(Settings.class);

	private static Settings _instance;

	private static final String DEFAULT_PROPERTIES_FILE_NAME = "pdp.properties";

	private final String _propertiesFilename;


	// default values will be overridden with the values from the properties file
	private int _pepGPBListenerPortNum = 10001;
	private int _pepThriftListenerPortNum=20001;
	private int _pmpListenerPortNum = 10002;
	private int _pipListenerPort = 10003;

	private String _pipAddress = "localhost";
	private int _pipPort = 10004;
	private int _queueSize = 100;

	private boolean _pmpListenerEnabled = true;
	private boolean _pepGPBListenerEnabled = true;
	private boolean _pepThriftListenerEnabled = true;
	private boolean _pipListenerEnabled = true;

	EDistributedPipStrategy _distributedPipStrategy = EDistributedPipStrategy.PUSH;

	private Settings() {
		this(DEFAULT_PROPERTIES_FILE_NAME);
	}

	private Settings(String propertiesFilename) {
		_propertiesFilename = propertiesFilename;

		try {
			initProperties(propertiesFilename);
		} catch (IOException e) {
			_logger.warn("Unable to load properties file [" + _propertiesFilename + "]. Using defaults.");
		}

		loadProperties();
	}


	public static Settings getInstance() {
		return (_instance != null)
			? _instance
			: getInstance(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public static Settings getInstance(String propertiesFilename) {
		if (_instance != null) {
			throw new IllegalStateException("Settings have already been initialized and loaded. "
					+ "Only the first invocation of " + Settings.class + ".getInstance() might be invoked with a parameter.");
		}

		_instance = new Settings(propertiesFilename);

		return _instance;
	}


	private void loadProperties() {
		_pmpListenerEnabled = loadSetting("pmp_listener_enabled", _pmpListenerEnabled);
		_pepGPBListenerEnabled = loadSetting("pep_GPB_listener_enabled", _pepGPBListenerEnabled);
		_pepThriftListenerEnabled = loadSetting("pep_Thrift_listener_enabled", _pepThriftListenerEnabled);
		_pipListenerEnabled = loadSetting("pip_listener_enabled", _pipListenerEnabled);
		_pipAddress = loadSetting("pip_address", _pipAddress);
		_pmpListenerPortNum = loadSetting("pmp_listener_port_num", _pmpListenerPortNum);
		_pepGPBListenerPortNum = loadSetting("pep_GPB_listener_port_num", _pepGPBListenerPortNum);
		_pepThriftListenerPortNum = loadSetting("pep_Thrift_listener_port_num", _pepThriftListenerPortNum);
		_pipListenerPort = loadSetting("pip_listener_port_num", _pipListenerPort);
		_pipPort = loadSetting("pip_port_num", _pipPort);
		_queueSize = loadSetting("queue_size", _queueSize);

		try {
			_distributedPipStrategy = EDistributedPipStrategy.from((String) _props.get("distributed_pip_strategy"));
		}
		catch (Exception e) {
			_logger.warn("Cannot read property [" + "distributed_pip_strategy" + "]. Using default value [" + _distributedPipStrategy + "].");
		}
	}


	public String getPropertiesFileName() {
		return _propertiesFilename;
	}

	public int getPepGPBListenerPortNum() {
		return _pepGPBListenerPortNum;
	}

	public int getPepThriftListenerPortNum() {
		return _pepThriftListenerPortNum;
	}

	public int getPmpListenerPortNum() {
		return _pmpListenerPortNum;
	}

	public int getPipListenerPortNum() {
		return _pipListenerPort;
	}

	public String getPipAddress() {
		return _pipAddress;
	}

	public int getPipPortNum() {
		return _pipPort;
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

	public EDistributedPipStrategy getDistributedPipStrategy() {
		return _distributedPipStrategy;
	}
}

