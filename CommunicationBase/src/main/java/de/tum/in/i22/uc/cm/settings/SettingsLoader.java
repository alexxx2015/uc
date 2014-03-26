package de.tum.in.i22.uc.cm.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SettingsLoader {
	private static final Logger _logger = LoggerFactory.getLogger(SettingsLoader.class);

	protected Properties _props;

	/**
	 * Tries to load <code>propertiesFileName</code> from the directory where the jar file is executed.
	 * If the file is not present there, it will be loaded from the jar file itself.
	 * This enables to easily override default properties which are specified in the file which
	 * is placed in the resource folder in the jar.
	 *
	 * @param propertiesFileName Name of the properties file to be loaded.
	 * @return Properties object with loaded properties.
	 * @throws IOException In case the file cannot be loaded.
	 */
	void initProperties(String propertiesFileName) throws IOException {
		_logger.debug("Loading properties file: " + propertiesFileName);

		InputStream is = null;
		File file = null;
		boolean fileFound = false;
		try {
			file = new File(propertiesFileName);
			fileFound = file.exists();
			_logger.debug("Searching properties file " + propertiesFileName + " ... " + (fileFound ? "Found" : "Not found") + ".");

			if (!fileFound) {
				file = new File(new File("."), propertiesFileName);
				fileFound = file.exists();
				_logger.debug("Searching properties file " + propertiesFileName	+ " in jar parent directory ... "  + (fileFound ? "Found" : "Not found") + ".");
			}

			if (!fileFound) {
				_logger.debug("Searching properties file " + propertiesFileName	+ " in resources ... ");
				is = SettingsLoader.class.getClassLoader().getResourceAsStream(propertiesFileName);
			}


			if (fileFound && is == null) {
				is = new FileInputStream(file);
			}

			if (is == null) {
				throw new IOException("Properties file not found.");
			}

			// load a properties file
			_props = new Properties();
			// load all the properties from this file
			_props.load(is);
			_logger.debug("Properties file '" + propertiesFileName + "' loaded.");

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

	protected <T extends Object> T loadSettingFinalize(boolean success, String propName, T loadedValue, T defaultValue) {
		if (success) {
			_logger.info("Property [" + propName + "] loaded. Value: [" + loadedValue + "].");
			return loadedValue;
		}
		else {
			_logger.warn("Cannot read property [" + propName + "]. Using default value [" + defaultValue + "].");
			return defaultValue;
		}
	}

	protected int loadSetting(String propName, int defaultValue) {
		int loadedValue = defaultValue;

		boolean success = true;

		try {
			loadedValue = Integer.valueOf((String) _props.get(propName));
		}
		catch (Exception e) {
			success = false;
		}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	protected String loadSetting(String propName, String defaultValue) {
		String loadedValue = defaultValue;

		boolean success = false;

		try {
			loadedValue = (String) _props.get(propName);
			if (loadedValue != null) {
				success = true;
			}
		}
		catch (Exception e) { }

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}

	protected boolean loadSetting(String propName, boolean defaultValue) {
		boolean loadedValue = defaultValue;

		boolean success = false;

		try {
			String s = _props.getProperty(propName);
			if (s != null) {
				loadedValue = Boolean.valueOf(s);
				success = true;
			}
		}
		catch (Exception e) {	}

		return loadSettingFinalize(success, propName, loadedValue, defaultValue);
	}
}
