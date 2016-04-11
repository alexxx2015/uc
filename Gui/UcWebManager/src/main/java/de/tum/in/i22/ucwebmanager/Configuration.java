package de.tum.in.i22.ucwebmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.vaadin.server.VaadinServlet;

public class Configuration {
	private static final String PROPERTIES_FILE = "Config.properties";
	public static final String LOCAL_FILE_SEPARATOR="::";
	
	//Web app root directory
	public static final String WebAppRoot = VaadinServlet.getCurrent().getServletContext().getRealPath("/");
	
	// Return ths 'apps' directory path where all applications have to be stored
	// webapp/app/
	public static String getAppsRoot(){
		File f = new File(WebAppRoot+File.pathSeparator+"apps");
		if(!f.exists())
			f.mkdirs();
		return f.getAbsolutePath();
	}
	
	public static void getProperty(){
		InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(
				PROPERTIES_FILE);
		if(inputStream != null){
			Properties p = new Properties();
			try {
				p.load(inputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
