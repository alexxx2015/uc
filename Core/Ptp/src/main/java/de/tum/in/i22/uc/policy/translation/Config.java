/**
 * 
 */
package de.tum.in.i22.uc.policy.translation;

/**
 * @author Prachi
 *
 */

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
        configFile.load(new FileInputStream("src/main/resources/config.cfg"));		
	}
	catch(FileNotFoundException eta){
		System.out.println("error: "+eta.getLocalizedMessage());
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
   
   public static void main(String[] args){
	   try {
		Config cfg = new Config();
		String file = cfg.getProperty("domainmodel");
		System.out.println(file);
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }

}


