/**
 * 
 */
package de.tum.in.i22.uc.ptp.utilities;

/**
 * @author Prachi
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;
 
public class Config 
{
   Properties configFile;
   private static final Logger logger = LoggerFactory.getLogger(Config.class);
	
   private String resourcesDir = "";
   
   public Config() throws IOException
   {
	configFile = new java.util.Properties();
	
	String defaultSetting = Settings.getInstance().getPtpResources();
	String userDir = System.getProperty("user.dir");
	this.resourcesDir = userDir + File.separator + defaultSetting;
	defaultSetting = resourcesDir 
						+ File.separator+"translation"
						+File.separator+"config.cfg";
	try {	
        configFile.load(new FileInputStream(defaultSetting));		
	}
	catch(FileNotFoundException eta){
		logger.error("PTP resources load error.", eta);
	}
   }
 
   public Config(String configurationPath) throws IOException
   {
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
   
   public String getResourcesDir(){
	   String value = resourcesDir;
	   return value ;
   }
   
   public String getTranslationDir(){
	   String value = resourcesDir + File.separator + "translation";
	   return value;
   }
//   public static void main(String[] args){
//	   try {
//			Config cfg = new Config();
//			String file = cfg.getProperty("domainmodel");
//			System.out.println(file);
//		
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	   
//   }

}


