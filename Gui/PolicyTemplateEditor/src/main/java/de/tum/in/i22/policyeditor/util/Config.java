/**
 * 
 */
package de.tum.in.i22.policyeditor.util;

/**
 * @author Prachi
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
 
public class Config 
{
   Properties configFile;
   public Config() throws IOException
   {
	configFile = new java.util.Properties();
	try {	
		String configPath = System.getProperty("user.dir")+File.separator+"src"
				+File.separator
				+"main"+File.separator
				+"resources"+File.separator
				+ "config.cfg";
        configFile.load(new FileInputStream(configPath));		
	}
	catch(FileNotFoundException eta){
		System.out.println("error: "+eta);
	}
   }
 
   public Config(String configurationPath) throws IOException
   {
	configFile = new java.util.Properties();
	try {	
        configFile.load(new FileInputStream(configurationPath));		
	}
	catch(FileNotFoundException eta){
		System.out.println("error: "+eta.getLocalizedMessage());
	}
   }
   
   public String getProperty(String key)
   {
	String value = this.configFile.getProperty(key);		
	return value;
   }
}
