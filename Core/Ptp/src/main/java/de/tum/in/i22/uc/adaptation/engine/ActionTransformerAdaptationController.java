package de.tum.in.i22.uc.adaptation.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel;
import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel.RefinementType;
import de.tum.in.i22.uc.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.utilities.PublicMethods;
/**
 * @author Cipri
 *
 */
public class ActionTransformerAdaptationController {

	private static int sequenceCounter = 0;
	private static final Logger logger = LoggerFactory.getLogger(ActionTransformerAdaptationController.class);
	
	private static int updatedElementsCounter = 0;
	
	/**
	 * Used for detecting changes in the base domain model.
	 * Additionally, can be used for performance analysis and
	 * 
	 * @return
	 */
	public static int getUpdatedElements(){
		return updatedElementsCounter;
	}
	
	private static void incrementUpdateCounter(){
		updatedElementsCounter++;
	}
	
	/**
	 * The Base Domain Model.
	 * The results of the adaptation will be stored here.
	 */
	private DomainModel baseDm;
	
	/**
	 * The New Domain Model which possibly contains new definitions. 
	 */
	private DomainModel newDm;

	private WordnetEngine wordnetEngine;
	
	public ActionTransformerAdaptationController(DomainModel baseDomainModel, DomainModel newDomainModel) {
		this.baseDm = baseDomainModel;
		this.newDm = newDomainModel;
		this.wordnetEngine = WordnetEngine.getInstance();
	}
	
	/**
	 * BOTTOM-UP approach	
	 * STEP 0: merge domain models: Actions and Transformers
	 * @throws DomainMergeException
	 */
	public void mergeDomainModels() throws DomainMergeException{
		//Merge ISM
		mergeInnerLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		baseDm.getIsmLayer().filterActionTransformerSequences();
		logger.debug("ISM transformers merging complete");

		//Merge PSM
		mergeInnerLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		baseDm.getPsmLayer().filterActionTransformerSequences();
		logger.debug("PSM transformers merging complete");
		
		//Merge PIM
		mergeInnerLinksLayer(baseDm.getPimLayer(), newDm.getPimLayer());
		baseDm.getPimLayer().filterActionTransformerSequences();
		logger.debug("PIM actions merging complete");
	}


	private void mergeInnerLinksLayer(LayerModel baseLayer, LayerModel newLayer) throws DomainMergeException{
		ArrayList<ActionTransformerModel> newActionTransformers = newLayer.getActionTransformers();
		for(ActionTransformerModel newAT : newActionTransformers){
			addNewTransformerToBase(newAT, baseLayer);
		}
	}

	
	/**
	 * See ./doc/ paper - Algorithm2
	 * @param newAt
	 * @param baseLayer
	 * @throws DomainMergeException
	 */
	private void addNewTransformerToBase(ActionTransformerModel newAt,	LayerModel baseLayer) throws DomainMergeException {
		if(newAt.isMerged())
			return;
		
		//if the element is at a different level, then return
		if(!newAt.getLayerType().equals(baseLayer.getType()))
			return;
		
		for(ActionTransformerModel innerLink : newAt.getRefinements()){
			addNewTransformerToBase(innerLink, baseLayer);
		}
		
		for(ActionTransformerModel innerLink : newAt.getAssociationLinks()){
			addNewTransformerToBase(innerLink, baseLayer);
		}
		
		logger.debug("Try ADD NewTransformer: "+ newAt.toStringShort()+ " To Base: "+ baseLayer.getType());
		ActionTransformerModel baseAt = searchEquivalent(newAt, baseLayer);
		if(baseAt == null){
			logger.debug("created NewTransformer: " + newAt.toStringShort());
			baseAt = new ActionTransformerModel(newAt.getName(), newAt.getLayerType());
			baseAt.setParenLayer(baseLayer);
			baseAt.setRefinementType(newAt.getRefinementType());
			/* The parent system will be updated when the systems are merged.
			 * Each transformer must have a parent system.
			 */
			baseAt.setParentSystem(newAt.getParentSystem());
			baseLayer.addActionTransformer(baseAt);		
			incrementUpdateCounter();
		}
		else{
			logger.debug("Equivalent found: base "+ baseAt.toStringShort());
		}
		
		mergeRefinement(newAt, baseAt);
		
		updateInputAndOutputContainers(newAt, baseAt);
		updateSynonyms(newAt, baseAt);
		updateAssociations(newAt, baseAt);
		
		newAt.markAsMerged();
		logger.debug("ADD Success: "+ newAt.toString());
		
	}

	/** 
	 * Update Inner/Cross Set/Seq refinement
	 * @param newAt
	 * @param baseAt
	 */
	private void mergeRefinement(ActionTransformerModel newAt, ActionTransformerModel baseAt) {
		logger.debug("Try MERGE refinement - base: " + baseAt.toStringShort() + " new: "+ newAt.toStringShort());
		if(newAt.getRefinementType().equals(RefinementType.SET)){
			if(baseAt.getRefinementType().equals(RefinementType.SET)){
				mergeSet2Set(newAt, baseAt);
			}
			else if(baseAt.getRefinementType().equals(RefinementType.SEQ)){
				mergeSet2Seq(newAt, baseAt);
			}
		}
		else{
			//newAt.getRefinementType().equals(RefinementType.SEQ)
			if(baseAt.getRefinementType().equals(RefinementType.SET)){
				mergeSeq2Set(newAt, baseAt);
			}
			else if(baseAt.getRefinementType().equals(RefinementType.SEQ)){
				mergeSeq2Seq(newAt, baseAt);
			}
		}
		
	}

	private void mergeSeq2Seq(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		
		boolean equivalent = equivalentSequence(newAt, baseAt);
		if(equivalent){
			String beforeBase = baseAt.toString();
			mergeNewSequenceToBaseSequence(newAt, baseAt);
			logger.debug("Merge Seq2Seq MODIFIED base "+ beforeBase + "  TO "+ baseAt.toString());
		}
		else{
			/* keep both sequences. 
			 * rename new sequence and simply add it to the base domain model.
			 * the merging goes bottom up, and because we are using objects and not string,
			 * the parents will still be able to find the renamed sequence.
			 */
			String sequenceName = newAt.getName() + "#"+PublicMethods.timestamp();
			newAt.setName(sequenceName);
			//copy new sequence to base
			ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
			seq.setParentSystem(baseAt.getParentSystem());
			seq.setRefinementType(RefinementType.SEQ);
			for(ActionTransformerModel ref : newAt.getRefinements()){
				ActionTransformerModel baseRef = null;
				if(ref.getLayerType().equals(newAt.getLayerType())){
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
					baseRef = refLayer.getActionTransformer(ref);					
				}
				else{
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType()).getRefinementLayer();
					baseRef = refLayer.getActionTransformer(ref);
				}				
				
				seq.addRefinement(baseRef);
				baseDm.getLayer(baseAt.getLayerType()).addActionTransformer(seq);
				logger.debug("Merge Seq2Seq ADDED to base "+ seq.toString());
				incrementUpdateCounter();
			}
			
		}
		
	}

	/**
	 * The new transformer is a SEQ.
	 * The base transformer is a SET.
	 * The new transformer is renamed with a unique name.
	 * The new transformer is added as a refinement to the set refinement of the base.
	 * @param newAt
	 * @param baseAt
	 */
	private void mergeSeq2Set(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		String sequenceName = newAt.getName() + "#"+PublicMethods.timestamp();
		newAt.setName(sequenceName);
		//copy object
		ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
		seq.setParentSystem(baseAt.getParentSystem());
		seq.setRefinementType(RefinementType.SEQ);
		for(ActionTransformerModel ref : newAt.getRefinements()){
			ActionTransformerModel baseRef = baseAt.getRefinementByName(ref.getName());
			if(baseRef == null){
				if(ref.getLayerType().equals(baseAt.getLayerType())){
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
					baseRef = refLayer.getActionTransformer(ref);					
				}
				else{
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType()).getRefinementLayer();
					baseRef = refLayer.getActionTransformer(ref);
				}				
				logger.debug("Merge Seq2Set EXTENDED base "+ seq.toString() + "  WITH "+ baseRef.toString());
				seq.addRefinement(baseRef);
				incrementUpdateCounter();
			}	
		}
		
		baseAt.addRefinement(seq);	
		incrementUpdateCounter();
	}

	/**
	 * The base transformer is refined as SEQ.
	 * The new transformer is refined as SET.
	 * The base transformer is cloned into a pseudo transformer with a timestamped name.
	 * the base transformer is transformed into a set refinement.
	 * The sequence created before is added to the base refinement.
	 * All the refinement elements from the new are added to the base.
	 * <br>
	 * newAt.SET <br> 
	 * baseAt.SEQ
	 * @param newAt
	 * @param baseAt
	 */
	private void mergeSet2Seq(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		//create new sequence
		String sequenceName = baseAt.getName()+"#"+PublicMethods.timestamp();
		ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
		seq.setParentSystem(baseAt.getParentSystem());
		seq.setRefinementType(RefinementType.SEQ);
		for(ActionTransformerModel ref : baseAt.getRefinements()){
			seq.addRefinement(ref);
		}				
		//transform sequence in set and clear all refinement elements
		baseAt.setRefinementType(RefinementType.SET);
		baseAt.resetRefinement();
		//update the base element refinement
		baseAt.addRefinement(seq);
		incrementUpdateCounter();
		for(ActionTransformerModel ref : newAt.getRefinements()){
			baseAt.addRefinement(ref); //the refinement was cleared before
			incrementUpdateCounter();
		}		
	}

	/**
	 * All the refinement elements of the newAt are added to the refinement of the baseAt. 
	 * @param newAt.SET
	 * @param baseAt.SET
	 */
	private void mergeSet2Set(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		logger.debug("Merge Set2Set - new: "+ newAt.toStringShort() +" base: "+ baseAt.toStringShort());
		for(ActionTransformerModel ref : newAt.getRefinements()){
			ActionTransformerModel baseRef = baseAt.getRefinement(ref);
			if(baseRef == null){
				if(ref.getLayerType().equals(baseAt.getLayerType())){
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
					baseRef = refLayer.getActionTransformer(ref);					
				}
				else{
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType()).getRefinementLayer();
					baseRef = refLayer.getActionTransformer(ref);
				}				
				logger.debug("Merge Set2Set EXTENDED base "+ baseAt.toString() + "  WITH "+ baseRef.toString());
				baseAt.addRefinement(baseRef);
				incrementUpdateCounter();
			}			
		}
	}

	private void updateSynonyms(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		// update synonyms
		if(!baseAt.getName().equals(newAt.getName())){
			baseAt.addSynonym(newAt.getName());
			//rename for the newDC to optimize the search
			String newName = newAt.getName();
			newAt.setName(baseAt.getName());
			newAt.addSynonym(newName);
			String logMsg = "similiratity found: (base-new) " + baseAt.getName() +"-"+ newName;
			logger.info(logMsg);
		}
			
		for(String alias : newAt.getSynonyms()){
			baseAt.addSynonym(alias);
			incrementUpdateCounter();
		}
	}
	
	private void updateInputAndOutputContainers(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		//input params
		for(DataContainerModel dc : newAt.getInputParams()){
			boolean exists = baseAt.hasInputParam(dc);
			if(exists)
				continue;
			DataContainerModel in = baseDm.getLayer(newAt.getLayerType()).getDataContainer(dc);
			baseAt.addInputParam(in);
			incrementUpdateCounter();
		}
		//output
		for(DataContainerModel dc : newAt.getOutputParams()){
			boolean exists = baseAt.hasOutputParam(dc);
			if(exists)
				continue;
			DataContainerModel out = baseDm.getLayer(newAt.getLayerType()).getDataContainer(dc);
			baseAt.addOutputParam(out);
			incrementUpdateCounter();
		}
	}
	
	/**
	 * See doc/metamodel.png
	 * <br> PIM - actionassociation attribute
	 * <br> PSM - no association (empty list)
	 * <br> ISM - no association (empty list)
	 * @param newAt
	 * @param baseAt
	 */
	private void updateAssociations(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		for(ActionTransformerModel assoc : newAt.getAssociationLinks()){
			ActionTransformerModel baseAssoc = baseAt.getAssociationLink(assoc);
			if(baseAssoc == null){
				baseAssoc = baseDm.getLayer(baseAt.getLayerType()).getActionTransformer(assoc);
				baseAt.addAssociationLink(baseAssoc);
				incrementUpdateCounter();
				logger.debug("UPDATE association: base "+ baseAt.toString() + " EXTENDED with "+ baseAssoc);
			}
		}
	}
	
	/**
	 * Implements the equivalence relation between two transformers or two data elements.
	 * @param newAt
	 * @param baseLayer
	 * @return
	 * @throws DomainMergeException 
	 */
	private ActionTransformerModel searchEquivalent(ActionTransformerModel newAt, LayerModel baseLayer) throws DomainMergeException {
		ActionTransformerModel baseEquivalent = null;
		
		if(!baseLayer.getType().equals(LayerType.PIM)){
				if(newAt.getParentSystem()==null)
					throw new DomainMergeException("Please define a parent system for new element: "+ newAt.toString());
		}
		for(ActionTransformerModel baseE : baseLayer.getActionTransformers()){
			logger.debug("Search equivalent - base: " + baseE.toStringShort() + " new: "+ newAt.toStringShort());
			//a redundant safety type check
			if(!baseE.getLayerType().equals(newAt.getLayerType()))
				continue;
			
			boolean nameCheck = false;
			if(baseE.getName().equals(newAt.getName()))
				nameCheck = true;
			else if(baseE.alsoKnownAs(newAt.getName()))
				nameCheck = true;
			else if(newAt.alsoKnownAs(baseE.getName()))
				nameCheck = true;
				
		/*
		 * If a sequence does not have a parent system, merging uncertainties arise.
		 * Transformers from different systems can have the same names.
		 * In the baseDM copyFile for OS and in the newDM copyFile for Browser.
		 * If these transformers are refined by sequences and the sequences have the same name
		 * and we don't associate a sequence with a particular system, 
		 * then we don't know which sequence from which system should we update.
		 * It is unreasonable to apply the same sequence to all of them.
		 * The solution is to specify a system also for sequences.
		 * e.g. copyFile - for OS, copyFile - for VM.
		 * according to the definition these two are different because they pertain to different systems.
		 *    
		 */
			if(!baseE.getLayerType().equals(LayerType.PIM)){
				if(baseE.getParentSystem()==null)
					throw new DomainMergeException("Please define a parent system for base element: "+ baseE.toString());
			
				if(baseE.getParentSystem().equals(newAt.getParentSystem())){
					if(nameCheck){
						baseEquivalent = baseE;
						break;
					}
				}
			}
			
		}
		
		if(!baseLayer.getType().equals(LayerType.PIM))
			return baseEquivalent;

		/* applies only at PIM level
		 * */
		if(baseEquivalent != null)
			return baseEquivalent;
		
		//find the closest match - wordnet
		float relationRatioMax = WordnetEngine.MAX_ALLOWED_DISTANCE; 
		float relationRatio = relationRatioMax;
		for(ActionTransformerModel baseE : baseLayer.getActionTransformers()){
			logger.debug("Search wordnet equivalent - base: " + baseE.toStringShort() + " new: "+ newAt.toStringShort());
			//try to find some similarity between the concepts.
			relationRatio = wordnetEngine.getDistance(newAt.getName(), baseE.getName());
			//search in the aliases only if there was no direct match between the names
			if(relationRatio > WordnetEngine.EQUAL_DISTANCE){
				float relationRatioAliasBase = wordnetEngine.getBestDistance(newAt.getName(), baseE.getSynonyms());
				float relationRatioAliasNew = wordnetEngine.getBestDistance(baseE.getName(), newAt.getSynonyms());
				if(relationRatioAliasBase < relationRatio)
					relationRatio = relationRatioAliasBase;
				if(relationRatioAliasNew < relationRatio)
					relationRatio = relationRatioAliasBase;
			}
			
			if(relationRatio == WordnetEngine.EQUAL_DISTANCE){
				//do nothing - container already exists in the base
				baseEquivalent = baseE;					
				relationRatioMax = relationRatio;
				break;
			} else if (relationRatio < WordnetEngine.SIMILAR_VERB_MAX_DISTANCE){
				//do nothing - container already exists in the base
				if(relationRatio < relationRatioMax){
					baseEquivalent = baseE;
					relationRatioMax = relationRatio;
				}
			}else {
				if(relationRatio < relationRatioMax){
					relationRatioMax = relationRatio;
				}
			}
		}
		// if nothing was found, the result remains null
		return baseEquivalent;
	}
	
	

	

	/**
	 * The sequences sent as parameters are first minimized.
	 * Two sequences are equivalent if (and between conditions)  
	 * <br> 1. they have the same length
	 * <br> 2. all elements of newAt are found in baseAt
	 * <br> 3. the elements are in the same order
	 * @param newAt - SEQ refinement
	 * @param baseAt - SEQ refinement
	 * @return true if equivalent, false if different
	 */
	public static boolean equivalentSequence(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		int index = 0;			
		ArrayList<ActionTransformerModel> newSeq = extractMinimizedSequence(newAt);
		ArrayList<ActionTransformerModel> baseSeq = extractMinimizedSequence(baseAt);
		if(newSeq.size()!=baseSeq.size())
			return false;
		for(index=0; index<newSeq.size(); index++){
			ActionTransformerModel newE =  newSeq.get(index);
			ActionTransformerModel baseE = baseSeq.get(index);
			if(!newE.equals(baseE))
				return false;
		}
		return true;
	}

	/**
	 *  "minimized sequence" contains all the elements of a sequence in the same order 
	 *  but with no consecutive repeating elements.
	 * e.g. < a a b a c c d > -> < a b a c d >
	 * @param at
	 * @return
	 */
	public static ArrayList<ActionTransformerModel> extractMinimizedSequence(ActionTransformerModel at){
		ArrayList<ActionTransformerModel> uniqueElements = new ArrayList<ActionTransformerModel>();
		ArrayList<ActionTransformerModel> seq = at.getRefinements();
		if(seq.size()==0)
			return uniqueElements;
		ActionTransformerModel lastAdded = seq.get(0);
		uniqueElements.add(lastAdded);
		for(ActionTransformerModel element : seq){
			if(element.equals(lastAdded))
				continue;
			else{
				lastAdded = element;
				uniqueElements.add(lastAdded);
			}
		}
		return uniqueElements;
	}
	
	
	/**
	 * keep the shortest subsequence of consecutive repetitive elements from each seq
	 * <br> <a, b, b, a, a, c, c> and <a, a, b, a, a, c>
	 * <br> will be merged to <a, b, a, a, c>
	 * @param newAt
	 * @param baseAt
	 */
	private void mergeNewSequenceToBaseSequence(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		ArrayList<ActionTransformerModel> newSeq = newAt.getRefinements();
		ArrayList<ActionTransformerModel> baseSeq = baseAt.getRefinements();		
		
		int baseIndex = 0;
		int baseSize = baseSeq.size();
		int newIndex = 0;
		int newSize = newSeq.size();
		ActionTransformerModel lastBase = null;
		ActionTransformerModel lastNew = null;
		//get the sizes before so I can modify the array in the loop.
		for(baseIndex=0; baseIndex<baseSize; ){
			ActionTransformerModel baseE = baseSeq.get(baseIndex);
			ActionTransformerModel newE = newSeq.get(newIndex);
			if(baseE.equals(newE)){
				//this is always the first branch taken.
				//the sequences must be equivalent with the method equivalentSequence
				baseIndex++;
				newIndex++;
				lastBase = baseE;
				lastNew = newE;
				if(newIndex>=newSize)
					return;
				if(baseIndex>=baseSize)
					return;
			}
			else{
				if(baseE.equals(lastBase)){
					baseSeq.remove(baseIndex);
					baseSize = baseSeq.size();
					incrementUpdateCounter();
				}
				else if (newE.equals(lastNew)){
					newSeq.remove(newIndex);
					newSize = newSeq.size();
				}
			}
		}
		
	}
	

}
