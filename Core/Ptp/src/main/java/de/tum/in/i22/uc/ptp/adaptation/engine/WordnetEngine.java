package de.tum.in.i22.uc.ptp.adaptation.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/adaptation/engine/WordnetEngine.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.utilities.Config;
=======
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/adaptation/engine/WordnetEngine.java
import rita.RiWordNet;
import de.tum.in.i22.uc.utilities.PtpLogger;

public class WordnetEngine {

	private static WordnetEngine _instance = null;
	
	private static RiWordNet wordnet;
	
	public static final float MAX_ALLOWED_DISTANCE = 0.4f;
	public static final float SIMILAR_NOUN_MAX_DISTANCE = 0.2f;
	public static final float SIMILAR_VERB_MAX_DISTANCE = 0.3f;
	public static final float EQUAL_DISTANCE = 0.0f;
	
	private PtpLogger logger ;
	
	private WordnetEngine(){
		Config config = null;
		try {
			config = new Config();
		} catch (IOException e) {
			logger.error("Config loading failed", e);
		}
		String userDir = config.getUserDir();
		String file = userDir + File.separator + "src" + File.separator +"main"+ File.separator+"resources" + File.separator ;
		wordnet = new RiWordNet(file);
		logger = PtpLogger.adaptationLoggerInstance();
	}
	
	public static WordnetEngine getInstance(){
		if(_instance!=null)
			return _instance;
		_instance = new WordnetEngine();
		_instance.testDictionaries();
		return _instance;
	}
	
	/**
	 * Test dictionaries.
	 * If the wordnet tests fail or it does not work correctly,
     * it is because the files have been modified when you pushed to git.
	 * Please restore all ./java/main/resources/dict files from http://wordnet.princeton.edu/wordnet/download/current-version/#win
	 *	The source of the problem is the following: 
	 *  warning: LF will be replaced by CRLF in Core/Ptp/src/main/resources/dict/verb.exc.
	 *	The file will have its original line endings in your working directory.	 
	 */
	private void testDictionaries(){
		float val = getDistance("picture", "photo");
		if(val == 1.0f)
			throw new RuntimeException("Wordnet Dictionaries are corrupted. See ./doc/wordnet_READ_THIS.txt for more details");
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
		float distance = 1.0f;
		if(pos != null)
			distance = wordnet.getDistance(conceptA, conceptB, pos);
		String msg ="distance: "+ conceptA +"-"+ conceptB +": "+ distance;
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/adaptation/engine/WordnetEngine.java
		//logger.info(msg);
=======
		logger.infoLog(msg, null);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/adaptation/engine/WordnetEngine.java
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
		if(pos==null)
			return 1.0f;
		for(String conceptB : concepts){
			float distance = wordnet.getDistance(conceptA, conceptB, pos);
			String msg ="similarity: "+ conceptA +"-"+ conceptB +": "+ distance;
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/adaptation/engine/WordnetEngine.java
			//logger.info(msg);
=======
			logger.infoLog(msg, null);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/adaptation/engine/WordnetEngine.java
			if(distance < maxDistance)
				maxDistance = distance;
		}
		
		return maxDistance;
	}
	
}
