package de.tum.in.i22.ucwebmanager.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class TomcatConfig {
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	
	Properties prop;
	public TomcatConfig() {
		this.prop = new Properties();
	}
	public void setUsername(String username) {
		prop.setProperty(USERNAME, username);
	}
	public String getUsername() {
		return prop.getProperty(USERNAME);
	}
	public void setPassword(String password) {
		prop.setProperty(PASSWORD, password);
	}
	public String getPassword() {
		return prop.getProperty(PASSWORD);
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

	public void load(String filepath){
		
		InputStream input = null;
		try {
			input = new FileInputStream(filepath);
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
	public void save(String filepath){
		try {
			OutputStream output = new FileOutputStream(filepath);
			prop.store(output, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
