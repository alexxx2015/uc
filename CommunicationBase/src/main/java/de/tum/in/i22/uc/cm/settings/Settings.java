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

	private int _pmpListenerPort = 21001;
	private int _pipListenerPort = 21002;
	private int _pdpListenerPort = 21003;
	private int _anyListenerPort = 21004;

	private boolean _pmpListenerEnabled = true;
	private boolean _pipListenerEnabled = true;
	private boolean _pdpListenerEnabled = true;
	private boolean _anyListenerEnabled = true;

	private String _pipEventHandlerSuffix = "EventHandler";
	private String _pipEventHandlerPackage = "de.tum.in.i22.uc.pip.eventdef.";

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
		_pipEventHandlerPackage = loadSetting("pip_event_handler_package", _pipEventHandlerPackage);
		_pipEventHandlerSuffix = loadSetting("pip_event_handler_suffix", _pipEventHandlerSuffix);

		_pmpListenerEnabled = loadSetting("pmp_listener_enabled", _pmpListenerEnabled);
		_pipListenerEnabled = loadSetting("pip_listener_enabled", _pipListenerEnabled);
		_pdpListenerEnabled = loadSetting("pdp_listener_enabled", _pdpListenerEnabled);
		_anyListenerEnabled = loadSetting("any_listener_enabled", _anyListenerEnabled);

		_pmpListenerPort = loadSetting("pmp_listener_port", _pmpListenerPort);
		_pipListenerPort = loadSetting("pip_listener_port", _pipListenerPort);
		_pdpListenerPort = loadSetting("pdp_listener_port", _pdpListenerPort);
		_anyListenerPort = loadSetting("any_listener_port", _anyListenerPort);

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

	public int getPmpListenerPort() {
		return _pmpListenerPort;
	}

	public int getPipListenerPort() {
		return _pipListenerPort;
	}

	public int getPdpListenerPort() {
		return _pdpListenerPort;
	}

	public int getAnyListenerPort() {
		return _anyListenerPort;
	}

	public boolean isPmpListenerEnabled() {
		return _pmpListenerEnabled;
	}

	public boolean isPipListenerEnabled() {
		return _pipListenerEnabled;
	}

	public boolean isPdpListenerEnabled() {
		return _pdpListenerEnabled;
	}

	public boolean isAnyListenerEnabled() {
		return _anyListenerEnabled;
	}

	public EDistributedPipStrategy getDistributedPipStrategy() {
		return _distributedPipStrategy;
	}

	public String getPipEventHandlerPackage() {
		return _pipEventHandlerPackage;
	}

	public String getPipEventHandlerSuffix() {
		return _pipEventHandlerSuffix;
	}
}

