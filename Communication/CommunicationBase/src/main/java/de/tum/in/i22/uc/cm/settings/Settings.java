package de.tum.in.i22.uc.cm.settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ConsistencyLevel;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.Location.ELocation;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;

/**
 *
 * @author Florian Kelbert
 *
 * Settings are read from the specified properties file.
 * If no file is specified, file "uc.properties" is used.
 *
 */
public class Settings extends SettingsLoader {

	private static Logger _logger = LoggerFactory.getLogger(Settings.class);

	private static Settings _instance = null;

	private static String _propertiesFile = "uc.properties";

	public static final String PROP_NAME_pdpListenerPort = "pdpListenerPort";
	public static final String PROP_NAME_pmpListenerPort = "pmpListenerPort";
	public static final String PROP_NAME_pipListenerPort = "pipListenerPort";
	public static final String PROP_NAME_anyListenerPort = "anyListenerPort";

	public static final String PROP_NAME_pepListenerPort = "pepListenerPort";
	public static final String PROP_NAME_pxpListenerPort = "pxpListenerPort";
	public static final String PROP_NAME_anyListenerEnabled = "anyListenerEnabled";

	public static final String PROP_NAME_pdpLocation = "pdpLocation";
	public static final String PROP_NAME_pipLocation = "pipLocation";
	public static final String PROP_NAME_pmpLocation = "pmpLocation";

	public static final String PROP_NAME_pipEnabledInformationFlowModels = "pipEnabledInformationFlowModels";
	public static final String PROP_NAME_pipEventHandlerSuffix = "pipEventHandlerSuffix";
	public static final String PROP_NAME_pipEventHandlerPackage = "pipEventHandlerPackage";
	public static final String PROP_NAME_pipInitializerEvent = "pipInitializerEvent";
	public static final String PROP_NAME_pipPersistenceDirectory = "pipPersistenceDirectory";

	public static final String PROP_NAME_pipPrintAfterUpdate = "pipPrintAfterUpdate";

	public static final String PROP_NAME_separator1 = "separator1";
	public static final String PROP_NAME_separator2 = "separator2";
	public static final String PROP_NAME_pipInitialRepresentationSeparator1 = "pipInitialRepresentationSeparator1";
	public static final String PROP_NAME_pipInitialRepresentationSeparator2 = "pipInitialRepresentationSeparator2";

	public static final String PROP_NAME_prefixSeparator = "prefixSeparator";

	public static final String PROP_NAME_pep = "pep";
	public static final String PROP_NAME_allowImpliesActual = "allowImpliesActual";

	public static final String PROP_NAME_pipInitialRepresentations = "pipInitialRepresentations";

	public static final String PROP_NAME_connectionAttemptInterval = "connectionAttemptInterval";

	public static final String PROP_NAME_starEvent = "starEvent";

	public static final String PROP_NAME_scopeDelimiterName = "scopeDelimiterName";
	public static final String PROP_NAME_scopeOpenDelimiter = "scopeOpenDelimiter";
	public static final String PROP_NAME_scopeCloseDelimiter = "scopeCloseDelimiter";
	public static final String PROP_NAME_scopeDirectionName = "scopeDirectionName";
	public static final String PROP_NAME_scopeGenericInDirection = "scopeGenericInDirection";
	public static final String PROP_NAME_scopeGenericOutDirection = "scopeGenericOutDirection";

	public static final String PROP_NAME_showFullIFModel = "showFullIFModel";
	public static final String PROP_NAME_showIFNamesInsteadOfContainer = "showIFNamesInsteadOfContainers";
	public static final String PROP_NAME_sortStorageNames = "sortStorageNames";

	private static final String PROP_NAME_excelCoordinatesSeparator = "excelCoordinatesSeparator";
	private static final String PROP_NAME_excelListSeparator = "excelListSeparator";
	private static final String PROP_NAME_excelOcbName = "excelOcbName";
	private static final String PROP_NAME_excelScbName = "excelScbName";

	private static final String PROP_NAME_joanaDelimiter1 = "joanaDelimiter1";
	private static final String PROP_NAME_joanaDelimiter2 = "joanaDelimiter2";

	private static final String PROP_NAME_joanaPidPoiSeparator = "joanaPidPoiSeparator";

	private static final String PROP_NAME_cleanUpInterval = "cleanUpInterval";

	private static final String PROP_NAME_policySpecificationStarDataClass = "policySpecificationStarDataClass";

	private static final String PROP_NAME_pmpInitialPolicies = "pmpInitialPolicies";

	private static final String PROP_NAME_javaPipMonitor="javaPipMonitor";

	private static final String PROP_NAME_pdpJaxbContext = "pdpJaxbContext";
	private static final String PROP_NAME_pmpJaxbContext = "pmpJaxbContext";

	public static final String PROP_NAME_ptpResources = "ptpResources";
	public static final String PROP_NAME_ptEditorResources = "ptEditorResources";

	public static final String PROP_NAME_distributionEnabled = "distributionEnabled";
	public static final String PROP_NAME_distributionMaxPipConnections = "distributionMaxPipConnections";
	public static final String PROP_NAME_distributionMaxPdpConnections = "distributionMaxPdpConnections";
	public static final String PROP_NAME_distributionMaxPmpConnections = "distributionMaxPmpConnections";
	public static final String PROP_NAME_distributionConsistencyLevel = "distributionConsistencyLevel";



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

		if (!success) {
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
		loadSetting(PROP_NAME_pmpListenerPort, 21001);
		loadSetting(PROP_NAME_pipListenerPort, 21002);
		loadSetting(PROP_NAME_pdpListenerPort, 21003);
		loadSetting(PROP_NAME_anyListenerPort, 21004);

		loadSetting(PROP_NAME_pxpListenerPort, 30003);
		loadSetting(PROP_NAME_pepListenerPort, 30005);

		loadSetting(PROP_NAME_anyListenerEnabled, true);

		loadSetting(PROP_NAME_pdpLocation, LocalLocation.getInstance());
		loadSetting(PROP_NAME_pipLocation, LocalLocation.getInstance());
		loadSetting(PROP_NAME_pmpLocation, LocalLocation.getInstance());

		loadSetting(PROP_NAME_pipEnabledInformationFlowModels,
				"scope@structure");
		loadSetting(PROP_NAME_pipEventHandlerSuffix, "EventHandler");
		loadSetting(PROP_NAME_pipEventHandlerPackage,
				"de.tum.in.i22.uc.pip.eventdef.");
		loadSetting(PROP_NAME_pipInitializerEvent, "SchemaInitializer");
		loadSetting(PROP_NAME_pipPersistenceDirectory, "pipdb");

		loadSetting(PROP_NAME_pipPrintAfterUpdate, true);

		loadSetting(PROP_NAME_separator1, "@");
		loadSetting(PROP_NAME_separator2, "#");
		loadSetting(PROP_NAME_pipInitialRepresentationSeparator1, ";");
		loadSetting(PROP_NAME_pipInitialRepresentationSeparator2, ":");

		loadSetting(PROP_NAME_prefixSeparator, "_");

		loadSetting(PROP_NAME_pep, "PEP");
		loadSetting(PROP_NAME_allowImpliesActual,
				"false");

		loadSetting(PROP_NAME_pipInitialRepresentations,
				new HashMap<IName, IData>() {
			private static final long serialVersionUID = -2810488356921449504L;
			{
				put(new NameBasic("TEST_C"), new DataBasic("TEST_D"));
			}
		});

		loadSetting(PROP_NAME_distributionEnabled, false);
		loadSetting(PROP_NAME_distributionMaxPipConnections, 5);
		loadSetting(PROP_NAME_distributionMaxPdpConnections, 5);
		loadSetting(PROP_NAME_distributionMaxPmpConnections, 5);
		loadSetting(PROP_NAME_distributionConsistencyLevel, ConsistencyLevel.ALL, ConsistencyLevel.class);

		loadSetting(PROP_NAME_connectionAttemptInterval, 1000);

		loadSetting(PROP_NAME_starEvent, "*");

		loadSetting(PROP_NAME_scopeDelimiterName, "delimiter");
		loadSetting(PROP_NAME_scopeOpenDelimiter, "START");
		loadSetting(PROP_NAME_scopeCloseDelimiter, "END");
		loadSetting(PROP_NAME_scopeDirectionName, "direction");
		loadSetting(PROP_NAME_scopeGenericInDirection, "IN");
		loadSetting(PROP_NAME_scopeGenericOutDirection, "OUT");

		loadSetting(PROP_NAME_policySpecificationStarDataClass, "*");

		loadSetting(PROP_NAME_showFullIFModel, true);
		loadSetting(PROP_NAME_showIFNamesInsteadOfContainer, true);
		loadSetting(PROP_NAME_sortStorageNames, true);

		loadSetting(PROP_NAME_excelCoordinatesSeparator, "!");
		loadSetting(PROP_NAME_excelListSeparator, "\\*");

		loadSetting(PROP_NAME_excelOcbName, "OfficeClipboard");
		loadSetting(PROP_NAME_excelScbName, "SystemClipboard(Excel)");

		loadSetting(PROP_NAME_joanaDelimiter1, ":");
		loadSetting(PROP_NAME_joanaDelimiter2, "#");
		loadSetting(PROP_NAME_joanaPidPoiSeparator, "--");

		loadSetting(PROP_NAME_cleanUpInterval, 10000);

		loadSetting(PROP_NAME_pmpInitialPolicies, ":", new HashSet<String>());

		loadSetting(PROP_NAME_pdpJaxbContext, "de.tum.in.i22.uc.pdp.xsd");
		loadSetting(PROP_NAME_pmpJaxbContext, "de.tum.in.i22.uc.pmp.xsd");

		loadSetting(PROP_NAME_ptpResources, ".");
		loadSetting(PROP_NAME_ptEditorResources, ".");

		loadSetting(PROP_NAME_javaPipMonitor, true);
	}

	private Location loadSetting(String propName, Location defaultValue) {
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

	private <E extends Enum<E>> E loadSetting(String propName, E defaultValue, Class<E> cls) {
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
	 * Gets the property with the specified name (a string), splits its value using the specified
	 * delimiter and returns the result of the splitting as a set.
	 *
	 * @param propName
	 * @param delimiter
	 * @param defaultValue
	 * @return
	 */
	private Set<String> loadSetting(String propName, String delimiter, Set<String> defaultValue) {
		Set<String> loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = new HashSet<String>(Arrays.asList(((String) _props.get(propName)).split(delimiter)));

			if (loadedValue != null && loadedValue.size() > 0) {
				success = true;
			}
		} catch (Exception e) {
			success = false;
		}

		return loadSettingFinalize(success, propName, Collections.unmodifiableSet(loadedValue), Collections.unmodifiableSet(defaultValue));
	}

	/**
	 * Loads the initial representations for the Pip. They are expected to be in
	 * the format <ContainerName1>:<DataId1>;<ContainerName2>:<DataId2>; ...
	 *
	 * Separators : and ; may be adjusted/changed by options
	 * PROP_NAME_pipInitialRepresentationSeparator1 and
	 * PROP_NAME_pipInitialRepresentationSeparator2
	 *
	 * @param propName
	 *            the property name
	 * @param defaultValue
	 * @return
	 */
	private Map<IName, IData> loadSetting(String propName,
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
			String[] entries = stringRead
					.split(getPipInitialRepresentationSeparator1());

			if (entries != null && entries.length > 0) {

				for (String entry : entries) {

					// each entry is divided by exactly one colon
					// first part: container name; second part: data ID
					String[] entryParts = entry
							.split(getPipInitialRepresentationSeparator2());
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

		return loadSettingFinalize(success, propName, Collections.unmodifiableMap(loadedValue), Collections.unmodifiableMap(defaultValue));
	}

	public int getPmpListenerPort() {
		return getValue(PROP_NAME_pmpListenerPort);
	}

	public int getPipListenerPort() {
		return getValue(PROP_NAME_pipListenerPort);
	}

	public int getPepListenerPort() {
		return getValue(PROP_NAME_pepListenerPort);
	}

	public int getPdpListenerPort() {
		return getValue(PROP_NAME_pdpListenerPort);
	}

	public int getPxpListenerPort() {
		return getValue(PROP_NAME_pxpListenerPort);
	}

	public int getAnyListenerPort() {
		return getValue(PROP_NAME_anyListenerPort);
	}

	public boolean isPmpListenerEnabled() {
		Location l = getValue(PROP_NAME_pmpLocation);
		return l == null ? false : l.getLocation() == ELocation.LOCAL;
	}

	public boolean isPipListenerEnabled() {
		Location l = getValue(PROP_NAME_pipLocation);
		return l == null ? false : l.getLocation() == ELocation.LOCAL;
	}

	public boolean isPdpListenerEnabled() {
		Location l = getValue(PROP_NAME_pdpLocation);
		return l == null ? false : l.getLocation() == ELocation.LOCAL;
	}

	public boolean isAnyListenerEnabled() {
		return getValue(PROP_NAME_anyListenerEnabled);
	}

	public String getPipEventHandlerPackage() {
		return getValue(PROP_NAME_pipEventHandlerPackage);
	}

	public String getPipEventHandlerSuffix() {
		return getValue(PROP_NAME_pipEventHandlerSuffix);
	}

	public Set<EInformationFlowModel> getEnabledInformationFlowModels() {
		return EInformationFlowModel
				.from((String) getValue(PROP_NAME_pipEnabledInformationFlowModels));
	}

	public Location getPdpLocation() {
		return getValue(PROP_NAME_pdpLocation);
	}

	public Location getPipLocation() {
		return getValue(PROP_NAME_pipLocation);
	}

	public Location getPmpLocation() {
		return getValue(PROP_NAME_pmpLocation);
	}

	public String getPipInitializerEvent() {
		return getValue(PROP_NAME_pipInitializerEvent);
	}

	public String getPipPersistenceDirectory() {
		return getValue(PROP_NAME_pipPersistenceDirectory);
	}

	public boolean getPipPrintAfterUpdate() {
		return getValue(PROP_NAME_pipPrintAfterUpdate);
	}

	public Map<IName, IData> getPipInitialRepresentations() {
		return getValue(PROP_NAME_pipInitialRepresentations);
	}

	public int getDistributionMaxPipConnections() {
		return getValue(PROP_NAME_distributionMaxPipConnections);
	}

	public boolean getDistributionEnabled() {
		return getValue(PROP_NAME_distributionEnabled);
	}

	public int getDistributionMaxPdpConnections() {
		return getValue(PROP_NAME_distributionMaxPdpConnections);
	}

	public int getDistributionMaxPmpConnections() {
		return getValue(PROP_NAME_distributionMaxPmpConnections);
	}

	public ConsistencyLevel getDistributionConsistencyLevel() {
		return getValue(PROP_NAME_distributionConsistencyLevel);
	}

	public int getConnectionAttemptInterval() {
		return getValue(PROP_NAME_connectionAttemptInterval);
	}

	public String getSeparator1() {
		return getValue(PROP_NAME_separator1);
	}

	public String getSeparator2() {
		return getValue(PROP_NAME_separator2);
	}

	public String getPrefixSeparator() {
		return getValue(PROP_NAME_prefixSeparator);
	}

	public String getPep() {
		return getValue(PROP_NAME_pep);
	}

	public String getAllowImpliesActual() {
		return getValue(PROP_NAME_allowImpliesActual);
	}

	public String getPipInitialRepresentationSeparator1() {
		return getValue(PROP_NAME_pipInitialRepresentationSeparator1);
	}

	public String getPipInitialRepresentationSeparator2() {
		return getValue(PROP_NAME_pipInitialRepresentationSeparator2);
	}

	public String getStarEvent() {
		return getValue(PROP_NAME_starEvent);
	}

	public String getScopeDelimiterName() {
		return getValue(PROP_NAME_scopeDelimiterName);
	}

	public String getScopeOpenDelimiter() {
		return getValue(PROP_NAME_scopeOpenDelimiter);
	}

	public String getScopeCloseDelimiter() {
		return getValue(PROP_NAME_scopeCloseDelimiter);
	}

	public String getScopeDirectionName() {
		return getValue(PROP_NAME_scopeDirectionName);
	}

	public String getScopeGenericInDirection() {
		return getValue(PROP_NAME_scopeGenericInDirection);
	}

	public String getScopeGenericOutDirection() {
		return getValue(PROP_NAME_scopeGenericOutDirection);
	}

	public boolean getShowFullIFModel() {
		return getValue(PROP_NAME_showFullIFModel);
	}

	public String getPolicySpecificationStarDataClass() {
		return getValue(PROP_NAME_policySpecificationStarDataClass);
	}

	public boolean getShowIFNamesInsteadOfContainer(){
		return getValue(PROP_NAME_showIFNamesInsteadOfContainer);
	}

	public boolean getSortStorageNames(){
		return getValue(PROP_NAME_sortStorageNames);
	}

	public String getExcelCoordinatesSeparator(){
		return getValue(PROP_NAME_excelCoordinatesSeparator);
	}

	public String getExcelListSeparator(){
		return getValue(PROP_NAME_excelListSeparator);
	}

	public String getExcelOcbName(){
		return getValue(PROP_NAME_excelOcbName);
	}

	public String getExcelScbName(){
		return getValue(PROP_NAME_excelScbName);
	}

	public String getJoanaDelimiter1(){
		return getValue(PROP_NAME_joanaDelimiter1);
	}

	public String getJoanaDelimiter2(){
		return getValue(PROP_NAME_joanaDelimiter2);
	}

	public String getJoanaPidPoiSeparator(){
		return getValue(PROP_NAME_joanaPidPoiSeparator);
	}

	public int getCleanUpInterval(){
		return getValue(PROP_NAME_cleanUpInterval);
	}

	public Set<String> getPmpInitialPolicies() {
		return getValue(PROP_NAME_pmpInitialPolicies);
	}

	public String getPdpJaxbContext() {
		return getValue(PROP_NAME_pdpJaxbContext);
	}

	public String getPmpJaxbContext() {
		return getValue(PROP_NAME_pmpJaxbContext);
	}

	public String getPtpResources(){
		String path = getValue(PROP_NAME_ptpResources);

		// for Windows
		path = path.replace("\\", File.separator);

		// for Unix
		path = path.replace("/", File.separator);

		return path;
	}

	public String getPtEditorResources(){
		return getValue(PROP_NAME_ptEditorResources);
	}

	public boolean getJavaPipMonitor() {
		return getValue(PROP_NAME_javaPipMonitor);
	}
}
