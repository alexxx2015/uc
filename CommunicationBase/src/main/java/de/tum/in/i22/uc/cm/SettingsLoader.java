package de.tum.in.i22.uc.cm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SettingsLoader {
	
	
	private static final Logger _logger = Logger
			.getLogger(SettingsLoader.class);
	
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
	public static Properties loadProperties(String propertiesFileName) throws IOException {
		_logger.debug("Loading properties file: " + propertiesFileName);

		InputStream is = null;
		try {
			_logger.debug("Searching properties file " + propertiesFileName
					+ " in jar parent directory ...");
			File file = new File(new File("."), propertiesFileName);
			
			if (file.exists()) {
				_logger.debug("File found. Loading file ...");

				is = new FileInputStream(file);

			} else {
				_logger.debug("File not found. Loading file from resources ...");
				is = SettingsLoader.class.getClassLoader().getResourceAsStream(
						propertiesFileName);
			}

			if (is == null) {
				throw new IOException("Properties file not found.");
			}

			// load a properties file
			Properties props = new Properties();
			// load all the properties from this file
			props.load(is);
			_logger.debug("Properties file '" + propertiesFileName + "' loaded.");
			
			return props;
			
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
}
