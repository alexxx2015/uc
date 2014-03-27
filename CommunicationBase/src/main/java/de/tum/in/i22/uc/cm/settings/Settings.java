package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.distribution.IPLocation;
import de.tum.in.i22.uc.distribution.LocalLocation;
import de.tum.in.i22.uc.distribution.Location;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.pip.EInformationFlowModel;

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

	private static Settings _instance = null;

	private static String _propertiesFile = "pdp.properties";

	private int _pmpListenerPort = 21001;
	private int _pipListenerPort = 21002;
	private int _pdpListenerPort = 21003;
	private int _anyListenerPort = 21004;

	private boolean _pmpListenerEnabled = true;
	private boolean _pipListenerEnabled = true;
	private boolean _pdpListenerEnabled = true;
	private boolean _anyListenerEnabled = true;

	private Location _pdpLocation = new LocalLocation();
	private Location _pipLocation = new LocalLocation();
	private Location _pmpLocation = new LocalLocation();

	private String _pipEnabledInformationFlowModels = "scope";

	private String _pipEventHandlerSuffix 			= "EventHandler";
	private String _pipEventHandlerPackage 			= "de.tum.in.i22.uc.pip.eventdef.";
	private String _pipInitializerEvent 			= "SchemaInitializer";
	private String _pipPersistenceDirectory			= "pipdb";
	private boolean _pipDeletePersistenceDirectory	= true;

	private EDistributedPipStrategy _distributedPipStrategy = EDistributedPipStrategy.PUSH;

	private Settings() {
		try {
			initProperties(_propertiesFile);
		} catch (IOException e) {
			_logger.warn("Unable to load properties file [" + _propertiesFile + "]. Using defaults.");
		}

		loadProperties();
	}

	public static void setPropertiesFile(String propertiesFile) {
		boolean success = false;
		if (_instance == null) {
			synchronized (Settings.class) {
				if (_instance == null) {
					_propertiesFile = propertiesFile;
					success = true;
				}
			}
		}

		if (success) {
			_logger.warn("Must set properties file before getting the first Settings instance.");
		}
	}

	public static Settings getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (Settings.class) {
				if (_instance == null) _instance = new Settings();
			}
		}
		return _instance;
	}

	private void loadProperties() {
		_pmpLocation = loadSetting("pmp_location", _pmpLocation);
		_pipLocation = loadSetting("pip_location", _pipLocation);
		_pdpLocation = loadSetting("pdp_location", _pdpLocation);

		_pmpListenerEnabled = loadSetting("pmp_listener_enabled", _pmpListenerEnabled);
		_pipListenerEnabled = loadSetting("pip_listener_enabled", _pipListenerEnabled);
		_pdpListenerEnabled = loadSetting("pdp_listener_enabled", _pdpListenerEnabled);
		_anyListenerEnabled = loadSetting("any_listener_enabled", _anyListenerEnabled);

		_pmpListenerPort = loadSetting("pmp_listener_port", _pmpListenerPort);
		_pipListenerPort = loadSetting("pip_listener_port", _pipListenerPort);
		_pdpListenerPort = loadSetting("pdp_listener_port", _pdpListenerPort);
		_anyListenerPort = loadSetting("any_listener_port", _anyListenerPort);

		_pipEventHandlerPackage 			= loadSetting("pip_event_handler_package", _pipEventHandlerPackage);
		_pipEventHandlerSuffix 				= loadSetting("pip_event_handler_suffix", _pipEventHandlerSuffix);
		_pipInitializerEvent 				= loadSetting("pip_initializer_event", _pipInitializerEvent);
		_pipEnabledInformationFlowModels 	= loadSetting("pip_enabled_information_flow_models", _pipEnabledInformationFlowModels);
		_pipPersistenceDirectory 			= loadSetting("pip_persistence_directory", _pipPersistenceDirectory);
		_pipDeletePersistenceDirectory 		= loadSetting("pip_empty_persistence_directory", _pipDeletePersistenceDirectory);

		_distributedPipStrategy = loadSetting("distributed_pip_strategy", _distributedPipStrategy);
	}

	private Location loadSetting(String propName, Location defaultValue) {
		Location loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = IPLocation.from(_props.getProperty(propName));
			if (loadedValue != null) {
				success = true;
			}
		}
		catch (Exception e) { }

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	private EDistributedPipStrategy loadSetting(String propName, EDistributedPipStrategy defaultValue) {
		EDistributedPipStrategy loadedValue = defaultValue;

		boolean success = true;

		try {
			loadedValue = EDistributedPipStrategy.from((String) _props.get(propName));
		}
		catch (Exception e) {
			success = false;
		}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	public String getPropertiesFileName() {
		return _propertiesFile;
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

	public Set<EInformationFlowModel> getEnabledInformationFlowModels() {
		return EInformationFlowModel.from(_pipEnabledInformationFlowModels);
	}

	public Location getPdpLocation() {
		return _pdpLocation;
	}

	public Location getPipLocation() {
		return _pipLocation;
	}

	public Location getPmpLocation() {
		return _pmpLocation;
	}

	public String getPipInitializerEvent() {
		return _pipInitializerEvent;
	}

	public String getPipPersistenceDirectory() {
		return _pipPersistenceDirectory;
	}

	public boolean pipDeletePersistenceDirectory() {
		return _pipDeletePersistenceDirectory;
	}
}

