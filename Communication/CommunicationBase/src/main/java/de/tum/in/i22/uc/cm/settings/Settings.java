package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.distribution.ECommunicationProtocol;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.IPLocation.ELocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;

/**
 *
 * @author Florian Kelbert
 * Settings are read from the specified properties file.
 * If no file is specified, file "uc.properties" is used.
 *
 */
public class Settings extends SettingsLoader {

	private static Logger _logger = LoggerFactory.getLogger(Settings.class);

	private static Settings _instance = null;

	private static String _propertiesFile = "uc.properties";

	private int _pmpListenerPort = 21001;
	private int _pipListenerPort = 21002;
	private int _pdpListenerPort = 21003;
	private int _anyListenerPort = 21004;

	private int _pxpListenerPort = 30003;
	private boolean _anyListenerEnabled = true;

	private Location _pdpLocation = new LocalLocation();
	private Location _pipLocation = new LocalLocation();
	private Location _pmpLocation = new LocalLocation();

	private String _pipEnabledInformationFlowModels = "scope";
	private String _pipEventHandlerSuffix 			= "EventHandler";
	private String _pipEventHandlerPackage 			= "de.tum.in.i22.uc.pip.eventdef.";
	private String _pipInitializerEvent 			= "SchemaInitializer";
	private String _pipPersistenceDirectory			= "pipdb";

	private Map<IName,IData> _pipInitialRepresentations = new HashMap<IName,IData>() {
		private static final long serialVersionUID = -2810488356921449504L;
	{
		put(new NameBasic("TEST_C"), new DataBasic("TEST_D"));
	}};

	private ECommunicationProtocol _communicationProtocol = ECommunicationProtocol.THRIFT;

	private EDistributionStrategy _distributionStrategy = EDistributionStrategy.PUSH;
	private int _pipDistributionMaxConnections = 5;

	private int _pdpDistributionMaxConnections = 5;

	private int _pmpDistributionMaxConnections = 5;

	/**
	 * The amount of milliseconds to wait between two attempts to connect to a remote point
	 */
	private int _connectionAttemptInterval = 1000;


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

		_anyListenerEnabled = loadSetting("any_listener_enabled", _anyListenerEnabled);

		_pmpListenerPort = loadSetting("pmp_listener_port", _pmpListenerPort);
		_pipListenerPort = loadSetting("pip_listener_port", _pipListenerPort);
		_pdpListenerPort = loadSetting("pdp_listener_port", _pdpListenerPort);
		_anyListenerPort = loadSetting("any_listener_port", _anyListenerPort);

		_pxpListenerPort = loadSetting("pxp_listener_port", _pxpListenerPort);

		_pipEventHandlerPackage 			= loadSetting("pip_event_handler_package", _pipEventHandlerPackage);
		_pipEventHandlerSuffix 				= loadSetting("pip_event_handler_suffix", _pipEventHandlerSuffix);
		_pipInitializerEvent 				= loadSetting("pip_initializer_event", _pipInitializerEvent);
		_pipEnabledInformationFlowModels 	= loadSetting("pip_enabled_information_flow_models", _pipEnabledInformationFlowModels);
		_pipPersistenceDirectory 			= loadSetting("pip_persistence_directory", _pipPersistenceDirectory);
		_pipInitialRepresentations			= loadSetting("pip_initial_representations", _pipInitialRepresentations);

		_distributionStrategy = loadSetting("pip_distribution_strategy", _distributionStrategy, EDistributionStrategy.class);
		_pipDistributionMaxConnections = loadSetting("pip_distribution_max_connections", _pipDistributionMaxConnections);

		_pdpDistributionMaxConnections = loadSetting("pdp_distribution_max_connections", _pdpDistributionMaxConnections);

		_pmpDistributionMaxConnections = loadSetting("pmp_distribution_max_connections", _pmpDistributionMaxConnections);

		_communicationProtocol = loadSetting("communication_protocol", _communicationProtocol, ECommunicationProtocol.class);

		_connectionAttemptInterval = loadSetting("connection_attempt_interval", _connectionAttemptInterval);
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


	private <E extends Enum<E>> E loadSetting(String propName, E defaultValue, Class<E> cls) {
		E loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = E.valueOf(cls, (String) _props.get(propName));
			if (loadedValue != null) {
				success = true;
			}
		}
		catch (Exception e) {
			success = false;
		}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}


	/**
	 * Loads the initial representations for the Pip.
	 * They are expected to be in the format
	 * <ContainerName1>:<DataId1>;<ContainerName2>:<DataId2>; ...
	 *
	 * @param propName the property name
	 * @param defaultValue
	 * @return
	 */
	private Map<IName,IData> loadSetting(String propName, Map<IName,IData> defaultValue) {
		Map<IName,IData> loadedValue = new HashMap<>();

		boolean success = false;
		String stringRead = null;

		try {
			stringRead = (String) _props.get(propName);
		}
		catch (Exception e) {
			stringRead = null;
			success = false;
		}

		if (stringRead != null && stringRead.length() > 0) {

			// entries are divided by semicolon (;)
			String[] entries = stringRead.split(";");

			if (entries != null && entries.length > 0) {

				for (String entry : entries) {

					// each entry is divided by exactly one colon
					// first part: container name; second part: data ID
					String[] entryParts = entry.split(":");
					if (entryParts != null && entryParts.length == 2) {
						loadedValue.put(new NameBasic(entryParts[0]), new DataBasic(entryParts[1]));
					}
					else {
						_logger.debug("Incorrect entry format: " + entry);
					}
				}
			}
		}

		success = loadedValue.size() > 0;

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

	public int getPxpListenerPort() {
		return _pxpListenerPort;
	}
	public int getAnyListenerPort() {
		return _anyListenerPort;
	}

	public boolean isPmpListenerEnabled() {
		return _pmpLocation.getLocation() == ELocation.LOCAL;
	}

	public boolean isPipListenerEnabled() {
		return _pipLocation.getLocation() == ELocation.LOCAL;
	}

	public boolean isPdpListenerEnabled() {
		return _pdpLocation.getLocation() == ELocation.LOCAL;
	}

	public boolean isAnyListenerEnabled() {
		return _anyListenerEnabled;
	}

	public EDistributionStrategy getDistributionStrategy() {
		return _distributionStrategy;
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

	public ECommunicationProtocol getCommunicationProtocol() {
		return _communicationProtocol;
	}

	public Map<IName, IData> getPipInitialRepresentations() {
		return Collections.unmodifiableMap(_pipInitialRepresentations);
	}

	public int getPipDistributionMaxConnections() {
		return _pipDistributionMaxConnections;
	}

	public int getPdpDistributionMaxConnections() {
		return _pdpDistributionMaxConnections;
	}

	public int getPmpDistributionMaxConnections() {
		return _pmpDistributionMaxConnections;
	}

	public int getConnectionAttemptInterval() {
		return _connectionAttemptInterval ;
	}
}

