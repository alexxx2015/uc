package de.tum.in.i22.uc.adaptation;

import java.io.File;
import java.util.ArrayList;

import de.tum.in.i22.uc.utilities.PtpLogger;
import rita.RiWordNet;

public class WordnetEngine {

	private static WordnetEngine _instance = null;
	
	private static RiWordNet wordnet;
	
	public static final float MAX_ALLOWED_DISTANCE = 0.4f;
	public static final float VERY_SIMILAR_DISTANCE = 0.2f;
	public static final float EQUAL_DISTANCE = 0.0f;
	
	private PtpLogger logger ;
	
	private WordnetEngine(){
		String file = System.getProperty("user.dir") + File.separator + "support" + File.separator ;
		wordnet = new RiWordNet(file);
		logger = PtpLogger.adaptationLoggerInstance();
	}
	
	public static WordnetEngine getInstance(){
		if(_instance!=null)
			return _instance;
		_instance = new WordnetEngine();
		return _instance;
	}
	
	public float getDistance(String conceptA, String conceptB){
		String pos = wordnet.getBestPos(conceptA); 
		float distance = wordnet.getDistance(conceptA, conceptB, pos);
		String msg ="distance: "+ conceptA +"-"+ conceptB +": "+ distance;
		logger.infoLog(msg, null);
		if(distance > MAX_ALLOWED_DISTANCE)
			return 1.0f;
		return distance;
	}
	
	public float getBestDistance(String conceptA, ArrayList<String> concepts){
		String pos = wordnet.getBestPos(conceptA); 
		float maxDistance = MAX_ALLOWED_DISTANCE;
		for(String conceptB : concepts){
			float distance = wordnet.getDistance(conceptA, conceptB, pos);
			String msg ="similarity: "+ conceptA +"-"+ conceptB +": "+ distance;
			logger.infoLog(msg, null);
			if(distance < maxDistance)
				maxDistance = distance;
		}
		
		return maxDistance;
	}
	
}
