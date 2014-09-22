package de.tum.in.i22.uc.ptp.policy.customization;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cipri
 *
 */
public class FirefoxPepCompliance {

	private static final Logger _logger = LoggerFactory.getLogger(FirefoxPepCompliance.class);
	
	private String policy;
	private String backup;
	private Map<String,String> param;
	
	public static final String id = "firefox";

	public FirefoxPepCompliance(String xmlPolicy, Map<String, String> parameters){
		this.policy = xmlPolicy;
		this.backup = xmlPolicy;
		this.param = parameters;
	}
	
	public String getCompliantPolicy(){
		//TODO: add scope
		
		return this.policy;
	}
	
}
