/**
 * 
 */
package de.tum.in.i22.uc.policy.translation;

/**
 * @author Prachi
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import de.tum.in.i22.uc.cm.settings.Settings;
 
public class Config 
{
   Properties configFile;
   
   private String userDir = "";
   
   public Config() throws IOException
   {
	configFile = new java.util.Properties();
	
	String defaultSetting = Settings.getInstance().getPtpProjectLocation();
	String usrDir = System.getProperty("user.dir");
	if(usrDir.endsWith("Ptp"))
		defaultSetting = usrDir;
	else
		defaultSetting = usrDir + File.separator + defaultSetting;
	userDir = defaultSetting;
	defaultSetting += File.separator+"src"+File.separator+"main"
					+File.separator+"resources"
					+File.separator+"translation"
					+File.separator+"config.cfg";
	try {	
        configFile.load(new FileInputStream(defaultSetting));		
	}
	catch(FileNotFoundException eta){
		System.out.println("user directory: " + System.getProperty("user.dir"));
		System.out.println("error: "+eta.getLocalizedMessage());
	}
   }
 
   public Config(String configurationPath) throws IOException
   {
	configFile = new java.util.Properties();
	String defaultSetting = Settings.getInstance().getPtpProjectLocation();
	String usrDir = System.getProperty("user.dir");
	if(usrDir.contains("Ptp"))
		defaultSetting = usrDir;
	else
		defaultSetting = usrDir + File.separator + defaultSetting;
	userDir = defaultSetting;
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
   
   /**
    * The system user directory property can change depending on the actual process which uses this component.
    * This method returns the actual path to the location of this project. 
    * @return
    */
   public String getUserDir(){
	   String value = userDir;
	   return value ;
   }
   
   public static void main(String[] args){
	   try {
		Config cfg = new Config();
		String file = cfg.getProperty("domainmodel");
		System.out.println(file);
		
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
   }

}


