package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class TomcatConfig {
	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	
	Properties prop;
	public TomcatConfig() {
		this.prop = new Properties();
	}
	public void setHost(String host){
		prop.setProperty(HOST, host);
	}
	public String getHost(){
		return prop.getProperty(HOST);
	}
	public void setPort(String port){
		prop.setProperty(PORT, port);
	}
	public String getPort(){
		return prop.getProperty(PORT);
	}

	public void load(String path){
		
		InputStream input = null;
		try {
			input = new FileInputStream(path);
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void save(String path){
		try {
			OutputStream output = new FileOutputStream(path);
			prop.store(output, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
