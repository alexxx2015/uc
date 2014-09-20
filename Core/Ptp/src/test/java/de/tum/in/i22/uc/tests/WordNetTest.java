package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.ptp.utilities.Config;
import rita.RiWordNet;
import rita.wordnet.jwnl.JWNLException;
import rita.wordnet.jwnl.dictionary.Dictionary;
import rita.wordnet.jwnl.wndata.IndexWord;
import rita.wordnet.jwnl.wndata.PointerType;
import rita.wordnet.jwnl.wndata.relationship.AsymmetricRelationship;
import rita.wordnet.jwnl.wndata.relationship.Relationship;
import rita.wordnet.jwnl.wndata.relationship.RelationshipFinder;
import rita.wordnet.jwnl.wndata.relationship.RelationshipList;


public class WordNetTest {

	private static RiWordNet wordnet;	
	/**
	 * The tests are disabled because they were used only to asses 
	 * the functionality of the external library.
	 * - speed, accuracy etc.
	 */
	 
	 /**
	 * If the wordnet tests fail or it does not work correctly,
     * it is because the files have been modified when you pushed to git.
	 * Please restore all ./java/main/resources/dict files from http://wordnet.princeton.edu/wordnet/download/current-version/#win
	 *	The source of the problem is the following: 
	 *  warning: LF will be replaced by CRLF in Core/Ptp/src/main/resources/dict/verb.exc.
	 *	The file will have its original line endings in your working directory.	 
	 */
	 
	private static final boolean TESTS_ENABLED = false;
	
	private static final String NOUN_POS = "n";
	private static final String VERB_POS = "v";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
		String file = "" ;
		
		Config conf = new Config();
		String userDir = conf.getUserDir();
		file = userDir + File.separator+ File.separator+ "src" 
				+ File.separator + "main"
				+ File.separator +"resources"
				+ File.separator + "dict" ;
		
		System.out.println("RitWordnet dict: "+ file);
		wordnet = new RiWordNet(file);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
	}

	@Test
	public void testWordEngine(){
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		float val = computeSimilarity("photo", "picture", NOUN_POS);
		assertTrue("The dictionary files have been corrupted. See commentary at the beggining of the test calss.", val == 0.0f);
	}
	
	@Test
	public void testWordsSimilarity() throws JWNLException {
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		
		System.out.println("\nNOUNS");
		computeSimilarity("photo", "picture", NOUN_POS);
		computeSimilarity("photo", "car", NOUN_POS);
		computeSimilarity("photo", "child", NOUN_POS);
		computeSimilarity("picture", "album", NOUN_POS);
		computeSimilarity("document", "file", NOUN_POS);
		computeSimilarity("directory", "folder", NOUN_POS);
		computeSimilarity("copy", "replica", NOUN_POS);
		computeSimilarity("picture", "photo", NOUN_POS);
		computeSimilarity("city", "residence", NOUN_POS);
		computeSimilarity("city", "town", NOUN_POS);
		computeSimilarity("home", "residence", NOUN_POS);		
		computeSimilarity("parent", "child", NOUN_POS);
		computeSimilarity("file", "directory", NOUN_POS);
		computeSimilarity("file", "folder", NOUN_POS);
		computeSimilarity("document", "folder", NOUN_POS);
		
		computeSimilarity("picture1", "photo1", NOUN_POS);
		
		System.out.println("\nVERBS");
		computeSimilarity("copy", "duplicate", VERB_POS);
		computeSimilarity("duplicate", "copy", VERB_POS);
		computeSimilarity("replicate", "duplicate", VERB_POS);
		computeSimilarity("duplicate", "replicate", VERB_POS);
		computeSimilarity("replicate", "copy", VERB_POS);
		computeSimilarity("copy", "replicate", VERB_POS);
		computeSimilarity("move", "relocate", VERB_POS);
		computeSimilarity("relocate", "move", VERB_POS);
		computeSimilarity("delete", "remove", VERB_POS);
		computeSimilarity("send", "distribute", VERB_POS);
		computeSimilarity("copy", "distribute", VERB_POS);
		computeSimilarity("print", "distribute", VERB_POS);
		computeSimilarity("shred", "delete", VERB_POS);
		computeSimilarity("shred", "destroy", VERB_POS);
	}

	@Test
	public void testSimilarityDelayTime(){
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		long start = System.currentTimeMillis();
		
		for(int i = 0; i<10; i++){
			computeSimilarity(null,null,"n");
			computeSimilarity(null,null,"v");
		}
		
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000 + " seconds");
	}
	
	public float computeSimilarity(String wordA, String wordB, String pos) {
		String conceptA = wordA;
		String conceptB = wordB;
		if(wordA == null)
			conceptA = wordnet.getRandomWord("n");
		if(wordB == null)
			conceptB = wordnet.getRandomWord("n");		
		String bestPos = wordnet.getBestPos(conceptA);
		float distance = wordnet.getDistance(conceptA, conceptB, pos);
		float distance2 = 1.0f;
		if(bestPos != null)
			distance2 = wordnet.getDistance(conceptA, conceptB, bestPos);
		String result = conceptA +"-"+ conceptB +": "+ distance + "("+pos+") : "+ distance2 +"("+ bestPos+")" ;
		System.out.println(result);
		return distance;
	}

	private void demonstrateAsymmetricRelationshipOperation(IndexWord start, IndexWord end) throws JWNLException {
		// Try to find a relationship between the first sense of <var>start</var> and the first sense of <var>end</var>
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.HYPERNYM);
		System.out.println("Hypernym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Common Parent Index: " + ((AsymmetricRelationship) list.get(0)).getCommonParentIndex());
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}
	
	private void demonstrateSymmetricRelationshipOperation(IndexWord start, IndexWord end) throws JWNLException {
		// find all synonyms that <var>start</var> and <var>end</var> have in common
		RelationshipList list = RelationshipFinder.getInstance().findRelationships(start.getSense(1), end.getSense(1), PointerType.SIMILAR_TO);
		System.out.println("Synonym relationship between \"" + start.getLemma() + "\" and \"" + end.getLemma() + "\":");
		for (Iterator itr = list.iterator(); itr.hasNext();) {
			((Relationship) itr.next()).getNodeList().print();
		}
		System.out.println("Depth: " + ((Relationship) list.get(0)).getDepth());
	}
	
	@Test
	public void testRandomSynonyms() throws Exception {
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		
		Dictionary dict = wordnet.jwnlDict;
		
		// Get a random noun
		String word = wordnet.getRandomWord("n");
		// Get max 15 synonyms
		String[] synonyms = wordnet.getAllSynonyms(word, "n", 15);

		System.out.println("Random noun: " + word);
		if (synonyms != null) {
			// Sort alphabetically
			Arrays.sort(synonyms);
			for (int i = 0; i < synonyms.length; i++) {
				System.out.println("Synonym " + i + ": " + synonyms[i]);
			}
		} else {
			System.out.println("No synyonyms!");
		}
	}
	
}
