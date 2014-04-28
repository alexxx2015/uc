package de.tum.in.i22.uc.cm.settings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.distribution.ECommunicationProtocol;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.Location.ELocation;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;

/**
 * 
 * @author Florian Kelbert Settings are read from the specified properties file.
 *         If no file is specified, file "uc.properties" is used.
 * 
 */
public class Settings extends SettingsLoader {

	private static Logger _logger = LoggerFactory.getLogger(Settings.class);

	private static Settings _instance = null;

	private static String _propertiesFile = "uc.properties";

	/**
	 * The amount of milliseconds to wait between two attempts to connect to a
	 * remote point
	 */
	private int _connectionAttemptInterval = 1000;

	private Settings() {

		_settings = new HashMap<>();

		try {
			initProperties(_propertiesFile);
		} catch (IOException e) {
			_logger.warn("Unable to load properties file [" + _propertiesFile
					+ "]. Using defaults.");
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
		 * This implementation may seem odd, overengineered, redundant, or all
		 * of it. Yet, it is the best way to implement a thread-safe singleton,
		 * cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes
		 * -with-example-code -FK-
		 */
		if (_instance == null) {
			synchronized (Settings.class) {
				if (_instance == null)
					_instance = new Settings();
			}
		}
		return _instance;
	}

	private void loadProperties() {

		loadSetting("pdpListenerPort", 100);
		loadSetting("pmpListenerPort", 21001);
		loadSetting("pipListenerPort", 21002);
		loadSetting("pdpListenerPort", 21003);
		loadSetting("anyListenerPort", 21004);

		loadSetting("pxpListenerPort", 30003);
		loadSetting("anyListenerEnabled", true);

		loadSetting("pdpLocation", LocalLocation.getInstance());
		loadSetting("pipLocation", LocalLocation.getInstance());
		loadSetting("pmpLocation", LocalLocation.getInstance());

		loadSetting("pipEnabledInformationFlowModels", "scope");
		loadSetting("pipEventHandlerSuffix", "EventHandler");
		loadSetting("pipEventHandlerPackage", "de.tum.in.i22.uc.pip.eventdef.");
		loadSetting("pipInitializerEvent", "SchemaInitializer");
		loadSetting("pipPersistenceDirectory", "pipdb");

		loadSetting("pipPrintAfterUpdate", true);

		loadSetting("separator1", "@");
		loadSetting("separator2", "#");
		loadSetting("prefixSeparator", "_");

		loadSetting("pepParameterKey", "PEP");
		loadSetting("allowImpliesActualParameterKey", "allowImpliesActual");

		loadSetting("pipInitialRepresentations", new HashMap<IName, IData>() {
			private static final long serialVersionUID = -2810488356921449504L;
			{
				put(new NameBasic("TEST_C"), new DataBasic("TEST_D"));
			}
		});

		loadSetting("communicationProtocol", ECommunicationProtocol.THRIFT,
				ECommunicationProtocol.class);

		loadSetting("distributionStrategy", EDistributionStrategy.PUSH,
				EDistributionStrategy.class);
		loadSetting("pipDistributionMaxConnections", 5);

		loadSetting("pdpDistributionMaxConnections", 5);

		loadSetting("pmpDistributionMaxConnections", 5);

	}

	public Location loadSetting(String propName, Location defaultValue) {
		Location loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = new IPLocation(_props.getProperty(propName));
			if (loadedValue != null) {
				success = true;
			}
		} catch (Exception e) {
		}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	public <E extends Enum<E>> E loadSetting(String propName, E defaultValue,
			Class<E> cls) {
		E loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = E.valueOf(cls, (String) _props.get(propName));
			if (loadedValue != null) {
				success = true;
			}
		} catch (Exception e) {
			success = false;
		}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	/**
	 * Loads the initial representations for the Pip. They are expected to be in
	 * the format <ContainerName1>:<DataId1>;<ContainerName2>:<DataId2>; ...
	 * 
	 * @param propName
	 *            the property name
	 * @param defaultValue
	 * @return
	 */
	public Map<IName, IData> loadSetting(String propName,
			Map<IName, IData> defaultValue) {
		Map<IName, IData> loadedValue = new HashMap<>();

		boolean success = false;
		String stringRead = null;

		try {
			stringRead = (String) _props.get(propName);
		} catch (Exception e) {
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
						loadedValue.put(new NameBasic(entryParts[0]),
								new DataBasic(entryParts[1]));
					} else {
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
		return getValue("pmpListenerPort");
	}

	public int getPipListenerPort() {
		return getValue("pipListenerPort");
	}

	public int getPdpListenerPort() {
		return getValue("pdpListenerPort");
	}

	public int getPxpListenerPort() {
		return getValue("pxpListenerPort");
	}

	public int getAnyListenerPort() {
		return getValue("anyListenerPort");
	}

	public boolean isPmpListenerEnabled() {
		Location l = getValue("pmpLocation");
		return ((l == null) ? false : (l.getLocation() == ELocation.LOCAL));
	}

	public boolean isPipListenerEnabled() {
		Location l = getValue("pipLocation");
		return ((l == null) ? false : (l.getLocation() == ELocation.LOCAL));
	}

	public boolean isPdpListenerEnabled() {
		Location l = getValue("pdpLocation");
		return ((l == null) ? false : (l.getLocation() == ELocation.LOCAL));
	}

	public boolean isAnyListenerEnabled() {
		return getValue("anyListenerEnabled");
	}

	public EDistributionStrategy getDistributionStrategy() {
		return getValue("distributionStrategy");
	}

	public String getPipEventHandlerPackage() {
		return getValue("pipEventHandlerPackage");
	}

	public String getPipEventHandlerSuffix() {
		return getValue("pipEventHandlerSuffix");
	}

	public Set<EInformationFlowModel> getEnabledInformationFlowModels() {
		return EInformationFlowModel.from((String)getValue("pipEnabledInformationFlowModels"));
	}

	public Location getPdpLocation() {
		return getValue("pdpLocation");
	}

	public Location getPipLocation() {
		return getValue("pipLocation");
	}

	public Location getPmpLocation() {
		return getValue("pmpLocation");
	}

	public String getPipInitializerEvent() {
		return getValue("pipInitializerEvent");
	}

	public String getPipPersistenceDirectory() {
		return getValue("pipPersistenceDirectory");
	}

	public boolean getPipPrintAfterUpdate() {
		return getValue("pipPrintAfterUpdate");
	}

	public ECommunicationProtocol getCommunicationProtocol() {
		return getValue("communicationProtocol");
	}

	@SuppressWarnings("unchecked")
	public Map<IName, IData> getPipInitialRepresentations() {
		return Collections.unmodifiableMap((Map<IName, IData>)getValue("pipInitialRepresentations"));
	}

	public int getPipDistributionMaxConnections() {
		return getValue("pipDistributionMaxConnections");
	}

	public int getPdpDistributionMaxConnections() {
		return getValue("pdpDistributionMaxConnections");
	}

	public int getPmpDistributionMaxConnections() {
		return getValue("pmpDistributionMaxConnections");
	}

	public int getConnectionAttemptInterval() {
		return getValue("connectionAttemptInterval");
	}

	public String getSeparator1() {
		return getValue("separator1");
	}

	public String getSeparator2() {
		return getValue("separator2");
	}

	public String getPrefixSeparator() {
		return getValue("prefixSeparator");
	}

	public String getPepParameterKey() {
		return getValue("pepParameterKey");
	}

	public String getAllowImpliesActualParameterKey() {
		return getValue("allowImpliesActualParameterKey");
	}

}
