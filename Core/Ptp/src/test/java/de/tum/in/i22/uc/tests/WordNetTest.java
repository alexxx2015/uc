package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rita.RiWordNet;
import rita.wordnet.jwnl.JWNLException;
import rita.wordnet.jwnl.wndata.IndexWord;
import rita.wordnet.jwnl.wndata.PointerType;
import rita.wordnet.jwnl.wndata.relationship.AsymmetricRelationship;
import rita.wordnet.jwnl.wndata.relationship.Relationship;
import rita.wordnet.jwnl.wndata.relationship.RelationshipFinder;
import rita.wordnet.jwnl.wndata.relationship.RelationshipList;


public class WordNetTest {

	private static RiWordNet wordnet;	
	private static final boolean TESTS_ENABLED = false;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
		String file = "src/main/resources/" + "dict" ;
		wordnet = new RiWordNet(file);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
	}

	@Test
	public void testWordsSimilarity() throws JWNLException {
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		computeSimilarity("picture", "album");
		computeSimilarity("document", "file");
		computeSimilarity("directory", "folder");
		computeSimilarity("copy", "replica");
		computeSimilarity("picture", "photo");
		
		computeSimilarity("parent", "child");
		computeSimilarity("file", "directory");
		computeSimilarity("file", "folder");
		computeSimilarity("document", "folder");
	}

	@Test
	public void testSimilarityDelayTime(){
		if(!TESTS_ENABLED){
			assertTrue("WordNetTest disabled", true);
			return;
		}
		long start = System.currentTimeMillis();
		
		for(int i = 0; i<10000; i++){
			computeSimilarity(null,null);
		}
		
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000 + " seconds");
	}
	
	public float computeSimilarity(String wordA, String wordB) {
		String conceptA = wordA;
		String conceptB = wordB;
		if(wordA == null)
			conceptA = wordnet.getRandomWord("n");
		if(wordB == null)
			conceptB = wordnet.getRandomWord("n");;
		String pos = wordnet.getBestPos(conceptA); 
		float distance = wordnet.getDistance(conceptA, conceptB, pos);
		String result = conceptA +"-"+ conceptB +": "+ distance;
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
