package de.tum.in.i22.uc.adaptation.engine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rita.RiWordNet;

public class WordnetEngine {

	private static WordnetEngine _instance = null;
	
	private static RiWordNet wordnet;
	
	public static final float MAX_ALLOWED_DISTANCE = 0.4f;
	public static final float VERY_SIMILAR_DISTANCE = 0.2f;
	public static final float EQUAL_DISTANCE = 0.0f;
	
	private static final Logger logger = LoggerFactory.getLogger(WordnetEngine.class);
	
	private WordnetEngine(){
		String file = "src/main/resources/" ;
		wordnet = new RiWordNet(file);
	}
	
	public static WordnetEngine getInstance(){
		if(_instance!=null)
			return _instance;
		_instance = new WordnetEngine();
		return _instance;
	}
	
	/** 
	 * A lower value means closer similarity between concepts.
	 * @param conceptA
	 * @param conceptB
	 * @return a value between 0.0 and 1.0<br>
	 * Everything that is above 0.4 is approximated to 1.0
	 */
	public float getDistance(String conceptA, String conceptB){
		String pos = wordnet.getBestPos(conceptA); 
		float distance = wordnet.getDistance(conceptA, conceptB, pos);
		String msg ="distance: "+ conceptA +"-"+ conceptB +": "+ distance;
		logger.info(msg);
		if(distance > MAX_ALLOWED_DISTANCE)
			return 1.0f;
		return distance;
	}
	
	/** A lower value means closer similarity between concepts.
	 * Only the closest distance between the conceptA and one concept from the list is returned.
	 * The best similarity is searched.
	 * @param conceptA
	 * @param concepts
	 * @return a value between 0.0 and 1.0<br>
	 * Everything that is above 0.4 is approximated to 1.0
	 */
	public float getBestDistance(String conceptA, ArrayList<String> concepts){
		String pos = wordnet.getBestPos(conceptA); 
		float maxDistance = MAX_ALLOWED_DISTANCE;
		for(String conceptB : concepts){
			float distance = wordnet.getDistance(conceptA, conceptB, pos);
			String msg ="similarity: "+ conceptA +"-"+ conceptB +": "+ distance;
			logger.info(msg);
			if(distance < maxDistance)
				maxDistance = distance;
		}
		
		return maxDistance;
	}
	
}
