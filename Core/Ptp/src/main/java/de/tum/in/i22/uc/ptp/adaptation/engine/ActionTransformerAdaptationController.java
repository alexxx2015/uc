package de.tum.in.i22.uc.ptp.adaptation.engine;

import java.util.ArrayList;

import org.hamcrest.core.IsSame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rita.RiString;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.ActionTransformerModel;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DataContainerModel;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DomainModel;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.LayerModel;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.SystemModel;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.ActionTransformerModel.RefinementType;
import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DomainModel.LayerType;
import de.tum.in.i22.uc.ptp.utilities.PublicMethods;
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
		updatedElementsCounter = 0;
		//Merge ISM
		mergeInnerLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		baseDm.getIsmLayer().filterActionTransformerSequences();
		//logger.debug("ISM transformers merging complete");

		//Merge PSM
		mergeInnerLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		baseDm.getPsmLayer().filterActionTransformerSequences();
		//logger.debug("PSM transformers merging complete");
		
		//Merge PIM
		mergeInnerLinksLayer(baseDm.getPimLayer(), newDm.getPimLayer());
		baseDm.getPimLayer().filterActionTransformerSequences();
		//logger.debug("PIM actions merging complete");
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
		
		for(ActionTransformerModel innerLink : newAt.getInnerRefinements()){
			addNewTransformerToBase(innerLink, baseLayer);
		}
		
		for(ActionTransformerModel innerLink : newAt.getAssociationLinks()){
			addNewTransformerToBase(innerLink, baseLayer);
		}
		
		logger.info("Try ADD NewTransformer: "+ newAt.toStringShort()+ " To Base: "+ baseLayer.getType());
		ActionTransformerModel baseAt = searchEquivalent(newAt, baseLayer);
		if(baseAt == null){
			logger.info("	created NewTransformer: " + newAt.toStringShort());
			baseAt = new ActionTransformerModel(newAt.getName(), newAt.getLayerType());
			baseAt.setRefinementType(newAt.getInnerRefinementType());
			baseLayer.addActionTransformer(baseAt);
			incrementUpdateCounter();
			
			if(!baseAt.getLayerType().equals(LayerType.PIM)){
				SystemModel baseSystem = baseLayer.getSystem(newAt.getParentSystem());
				if(baseSystem == null){
					/* The base system was not present before.
					 * It will be updated when the systems are merged.
					 * Each transformer must have a parent system.
					 */
					baseSystem = new SystemModel(newAt.getParentSystem().getName(),newAt.getLayerType());
					baseLayer.addSystem(baseSystem);
				}
				baseSystem.addOperation(baseAt);		
				incrementUpdateCounter();
			}
		}
		else{
			logger.info("	Equivalent found: base "+ baseAt.toStringShort());
		}
		
		
		if(newAt.getInnerRefinementType().equals(RefinementType.SET)){
			if(baseAt.getInnerRefinementType().equals(RefinementType.SET)){
				//synonyms first!! they must be updated first when is the case of equal/similar refinement
				updateSynonyms(newAt, baseAt);
				//input and output must be added to detect the signature!
				updateInputAndOutputContainers(newAt, baseAt);
				mergeInnerSet2Set(newAt, baseAt);
				mergeCrossRefinement(newAt, baseAt);
				updateAssociations(newAt, baseAt);
			}
			else if(baseAt.getInnerRefinementType().equals(RefinementType.SEQ)){
				mergeSet2Seq(newAt, baseAt);
			}
		}
		else{
			//newAt.getRefinementType().equals(RefinementType.SEQ)
			if(baseAt.getInnerRefinementType().equals(RefinementType.SET)){
				ActionTransformerModel newSquence = mergeSeq2Set(newAt, baseAt);
				//copying the elements from the new sequence.
				//the refinement, input, output, association list should be empty in the case of a sequence
				//added only for safety
				mergeCrossRefinement(newAt, newSquence);
				updateInputAndOutputContainers(newAt, newSquence);
				updateSynonyms(newAt, newSquence);
				updateAssociations(newAt, newSquence);
			}
			else if(baseAt.getInnerRefinementType().equals(RefinementType.SEQ)){
				mergeSeq2Seq(newAt, baseAt);
			}
		}
		
		removeRedundantInnerLinks(baseAt);
		
		newAt.markAsMerged();
		//logger.debug("ADD Success: "+ newAt.toString());
		
	}


	/**
	 *  Sequences can have different names but the same elements. 
	 *	Therefore, at this moment, an element could end up
	 *  with a set refinement composed of multiple sequences with different name,
	 *  but which could be in an equivalence or containment relation.
	 *  These sequences are processed in pair and merged according to the case presented before.
	 *  All redundant sequences are removed from the refinement of the element.
	 * @param baseAt
	 */
	private void removeRedundantInnerLinks(ActionTransformerModel baseAt) {
		if(!baseAt.getInnerRefinementType().equals(RefinementType.SET)){
			return;
		}
		
		ArrayList<ActionTransformerModel> refinements = baseAt.getInnerRefinements();
		int indexFirst = 0;
		int refSize = refinements.size(); //work with indexes to process the list on the fly
		for(indexFirst = 0; indexFirst < refSize-1; indexFirst++){
			ActionTransformerModel firstSequence = refinements.get(indexFirst);
			if(!firstSequence.getInnerRefinementType().equals(RefinementType.SEQ))
				continue;
			for(int indexSecond = indexFirst+1; indexSecond < refSize; indexSecond++){
				ActionTransformerModel secondSequence = refinements.get(indexSecond);
				if(!secondSequence.getInnerRefinementType().equals(RefinementType.SEQ))
					continue;
				//now we have two sequences which we can compare
				if(equivalentSequence(firstSequence, secondSequence)){
					mergeNewSequenceToBaseSequence(secondSequence, firstSequence);
					refinements.remove(indexSecond);
					refSize = refinements.size();
					indexSecond --; //decrease with one position to disable the ++
					logger.info("	remove from base "+ baseAt.toStringShort() + " refinement: "+ secondSequence.toStringShort());
					incrementUpdateCounter();
				}
			}
		}
	}

	private void mergeCrossRefinement(ActionTransformerModel newAt, ActionTransformerModel baseAt) throws DomainMergeException {
		//logger.debug("Merge Set2Set - new: "+ newAt.toStringShort() +" base: "+ baseAt.toStringShort());
		for(ActionTransformerModel ref : newAt.getCrossRefinements()){
			ActionTransformerModel baseRef = baseAt.getCrossRefinement(ref);
			if(baseRef == null){
				LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType()).getRefinementLayer();
				baseRef = refLayer.getActionTransformerEquivalent(ref);
				logger.debug("	Merge Cross Set2Set EXTENDED base "+ baseAt.toString() + "  WITH "+ baseRef.toString());
				baseAt.addCrossRefinement(baseRef);
				incrementUpdateCounter();
			}			
		}
		
	}
	
	private void mergeSeq2Seq(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		//the element was created before.
		boolean newSequence = baseAt.getInnerRefinements().size() == 0;
		if(newSequence){
			//copy new sequence to base
			for(ActionTransformerModel ref : newAt.getInnerRefinements()){
				ActionTransformerModel baseRef = null;
				LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
				baseRef = refLayer.getActionTransformerEquivalent(ref);
				baseAt.addInnerRefinement(baseRef);
				incrementUpdateCounter();
			}
			logger.debug("	Merge Seq2Seq ADDED to base "+ baseAt.toString());
			incrementUpdateCounter();
			return;
		}
		
		boolean equivalent = equivalentSequence(newAt, baseAt);
		if(equivalent){
			String beforeBase = baseAt.toString();
			mergeNewSequenceToBaseSequence(newAt, baseAt);
			logger.debug("	Merge Seq2Seq MODIFIED base "+ beforeBase + " TO "+ baseAt.toString());
		}
		else{
			/* keep both sequences. 
			 * rename new sequence and simply add it to the base domain model.
			 * the merging goes bottom up, and because we are using objects and not string,
			 * the parents will still be able to find the renamed sequence.
			 */
			boolean uniqueName = false;
			String sequenceName = newAt.getName();
			while(!uniqueName){
				sequenceName += "_"+baseDm.getLayer(baseAt.getLayerType()).getActionTransformers().size();
				newAt.setName(sequenceName);
				ActionTransformerModel found = null;
				found = baseDm.getLayer(baseAt.getLayerType()).getActionTransformer(sequenceName);
				if(found==null)
					uniqueName = true;
			}
			//copy new sequence to base
			ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
			SystemModel baseSystem = baseDm.getLayer(baseAt.getLayerType()).getSystem(newAt.getParentSystem());
			if(baseSystem == null){
				/* The base system was not present before.
				 * It will be updated when the systems are merged.
				 * Each transformer must have a parent system.
				 */
				baseSystem = new SystemModel(newAt.getParentSystem().getName(),newAt.getLayerType());
				baseDm.getLayer(baseAt.getLayerType()).addSystem(baseSystem);
			}
			baseSystem.addOperation(seq);
			
			seq.setRefinementType(RefinementType.SEQ);
			for(ActionTransformerModel ref : newAt.getInnerRefinements()){
				ActionTransformerModel baseRef = null;
				LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
				baseRef = refLayer.getActionTransformerEquivalent(ref);
				seq.addInnerRefinement(baseRef);
				incrementUpdateCounter();
			}
			
			baseDm.getLayer(baseAt.getLayerType()).addActionTransformer(seq);
			logger.debug("	Merge Seq2Seq ADDED to base "+ seq.toString());
			incrementUpdateCounter();
		}
		
	}

	/**
	 * The new transformer is a SEQ.
	 * The base transformer is a SET.
	 * The new transformer is renamed with a unique name.
	 * The new transformer is added as a refinement to the set refinement of the base.
	 * @param newAt
	 * @param baseAt
	 * @return 
	 */
	private ActionTransformerModel mergeSeq2Set(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		String sequenceName = newAt.getName();
		boolean uniqueName = false;
		while(!uniqueName){
			sequenceName += "_"+baseDm.getLayer(baseAt.getLayerType()).getActionTransformers().size();
			newAt.setName(sequenceName);
			ActionTransformerModel found = null;
			found = baseDm.getLayer(baseAt.getLayerType()).getActionTransformer(sequenceName);
			if(found==null)
				uniqueName = true;
		}
		//create new sequence. copy object
		ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
		
		SystemModel baseSystem = baseDm.getLayer(baseAt.getLayerType()).getSystem(newAt.getParentSystem());
		if(baseSystem == null){
			//this should never happen!
			/* The base system was not present before.
			 * It will be updated when the systems are merged.
			 * Each transformer must have a parent system.
			 */
			baseSystem = new SystemModel(newAt.getParentSystem().getName(),newAt.getLayerType());
			baseDm.getLayer(baseAt.getLayerType()).addSystem(baseSystem);
		}
		baseSystem.addOperation(seq);
		seq.setRefinementType(RefinementType.SEQ);
		
		LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
		refLayer.addActionTransformer(seq);
		//add refinements to sequence
		for(ActionTransformerModel ref : newAt.getInnerRefinements()){
				ActionTransformerModel baseRef = refLayer.getActionTransformerEquivalent(ref);					
				seq.addInnerRefinement(baseRef);
				incrementUpdateCounter();
		}
		
		baseAt.addInnerRefinement(seq);	
		logger.debug("	Merge Seq2Set EXTENDED base "+ baseAt.toString() + "  WITH "+ seq.toString());
		incrementUpdateCounter();
		
		return seq;
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
		String sequenceName = newAt.getName();
		boolean uniqueName = false;
		while(!uniqueName){
			sequenceName += "_"+baseDm.getLayer(baseAt.getLayerType()).getActionTransformers().size();
			newAt.setName(sequenceName);
			ActionTransformerModel found = null;
			found = baseDm.getLayer(baseAt.getLayerType()).getActionTransformer(sequenceName);
			if(found==null)
				uniqueName = true;
		}
		
		//create new sequence
		ActionTransformerModel seq = new ActionTransformerModel(sequenceName, baseAt.getLayerType());
		SystemModel baseSystem = baseDm.getLayer(baseAt.getLayerType()).getSystem(newAt.getParentSystem());
		if(baseSystem == null){
			/* The base system was not present before.
			 * It will be updated when the systems are merged.
			 * Each transformer must have a parent system.
			 */
			baseSystem = new SystemModel(newAt.getParentSystem().getName(),newAt.getLayerType());
			baseDm.getLayer(baseAt.getLayerType()).addSystem(baseSystem);
		}
		baseSystem.addOperation(seq);
		seq.setRefinementType(RefinementType.SEQ);
		for(ActionTransformerModel ref : baseAt.getInnerRefinements()){
			seq.addInnerRefinement(ref);
		}				
		//transform sequence in set and clear all refinement elements
		baseAt.setRefinementType(RefinementType.SET);
		baseAt.resetRefinement();
		//update the base element refinement
		baseAt.addInnerRefinement(seq);
		incrementUpdateCounter();
		for(ActionTransformerModel ref : newAt.getInnerRefinements()){
			baseAt.addInnerRefinement(ref); //the refinement was cleared before
			incrementUpdateCounter();
		}	
		baseDm.getLayer(baseAt.getLayerType()).addActionTransformer(seq);
		incrementUpdateCounter();
	}

	/**
	 * All the refinement elements of the newAt are added to the refinement of the baseAt. 
	 * @param newAt.SET
	 * @param baseAt.SET
	 */
	private void mergeInnerSet2Set(ActionTransformerModel newAt,	ActionTransformerModel baseAt) {
		//logger.debug("Merge Set2Set - new: "+ newAt.toStringShort() +" base: "+ baseAt.toStringShort());
		for(ActionTransformerModel ref : newAt.getInnerRefinements()){
			ActionTransformerModel baseRef = baseAt.getInnerRefinement(ref);
			if(baseRef == null){
				if(ref.getLayerType().equals(baseAt.getLayerType())){
					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType());
					baseRef = refLayer.getActionTransformerEquivalent(ref);					
				}
				// else branch should never happen!
//				else{
//					
//					LayerModel refLayer = baseDm.getLayer(baseAt.getLayerType()).getRefinementLayer();
//					baseRef = refLayer.getActionTransformer(ref);
//				}				
				logger.debug("	Merge Inner Set2Set EXTENDED base "+ baseAt.toString() + " WITH "+ baseRef.toString());
				baseAt.addInnerRefinement(baseRef);
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
			if(baseAt.addSynonym(alias))
				incrementUpdateCounter();
		}
	}
	
	private void updateInputAndOutputContainers(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		/*input params - this applies only at PIM
		 * at PSM, ISM - the signature is already equal
		 * or it is a newly created transformer.
		*/
		if(newAt.getLayerType().equals(LayerType.PIM)  
				|| newAt.getInputParams().size()!=baseAt.getInputParams().size()){
			for(DataContainerModel dc : newAt.getInputParams()){
				boolean exists = baseAt.hasInputParam(dc);
				if(exists)
					continue;
				DataContainerModel in = baseDm.getLayer(newAt.getLayerType()).getDataContainer(dc);
				baseAt.addInputParam(in);
				incrementUpdateCounter();
			}
		}
		else{
			/* applies at PSM and ISM
			 * replace the output param of base with new
			 * */
			baseAt.resetOutputParam();
			for(DataContainerModel dc : newAt.getOutputParams()){
				DataContainerModel out = baseDm.getLayer(newAt.getLayerType()).getDataContainer(dc);
				baseAt.addOutputParam(out);
				//incrementUpdateCounter();
			}
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
				baseAssoc = baseDm.getLayer(baseAt.getLayerType()).getActionTransformerEquivalent(assoc);
				baseAt.addAssociationLink(baseAssoc);
				incrementUpdateCounter();
				logger.debug("UPDATE association: base "+ baseAt.toString() + " EXTENDED with "+ baseAssoc);
			}
		}
	}
	
	/**
	 * Implements the equivalence relation between two transformers or two action elements.
	 * <br> equivalent name
	 * <br> equivalent systems - PSM, ISM
	 * <br> equivalent signature - PSM, ISM
	 * <br> equivalent synonyms
	 * <br> OR the same refinement (plus same signature and same system)
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
			//logger.debug("Search equivalent - base: " + baseE.toStringShort() + " new: "+ newAt.toStringShort());
			baseEquivalent = null;
			/* Applies at all three layers */
			boolean nameCheck = false;
			if(baseE.getName().equals(newAt.getName()))
				nameCheck = true;
			else if(baseE.alsoKnownAs(newAt.getName()))
				nameCheck = true;
			else if(newAt.alsoKnownAs(baseE.getName()))
				nameCheck = true;
					
			/* Applies at only PSM and ISM layers */
			if(!baseE.getLayerType().equals(LayerType.PIM)){
				if(baseE.getParentSystem()==null)
					throw new DomainMergeException("Please define a parent system for base element: "+ baseE.toString());
			
				//check system
				boolean systemCheck = false;
				if(baseE.getParentSystem().equals(newAt.getParentSystem())){
					systemCheck = true;
				}
				
				//check signature
				boolean signatureCheck = false;

				if(systemCheck){
					//same system.
					signatureCheck = ActionTransformerModel.equivalentTransformerSignature(newAt, baseE);
					if(signatureCheck){
						//same signature
						if(nameCheck){
							baseEquivalent = baseE;
							break;
						}
						else{
							//different name
							boolean equalRefinement = isEqualRefinement(newAt, baseE);
							if(equalRefinement){
								baseEquivalent = baseE;
								break;
							}
						}
					}
				}
			}
			else {
				if (nameCheck){
					//means that a PIM action was found
					baseEquivalent = baseE;
					break;
				}
				else {
					//different name
					boolean equalRefinement = isEqualRefinement(newAt, baseE);
					if(equalRefinement){
						baseEquivalent = baseE;
						break;
					}
				}
			}
		}
		
		if(!baseLayer.getType().equals(LayerType.PIM))
			return baseEquivalent; //this remains null if nothing was found

		/* applies only at PIM level
		 * */
		if(baseEquivalent != null)
			return baseEquivalent; //means that a PIM action was found
		
		//find the closest match - wordnet
		float relationRatioMax = WordnetEngine.MAX_ALLOWED_DISTANCE; 
		float relationRatio = relationRatioMax;
		for(ActionTransformerModel baseE : baseLayer.getActionTransformers()){
			//logger.debug("Search wordnet equivalent - base: " + baseE.toStringShort() + " new: "+ newAt.toStringShort());
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
	 * Two actions or transformers have the same refinement if
	 * <br> the inner refinement is of the same type (where is the case)
	 * <br> the inner refinement has exactly the same elements (where is the case)
	 * <br> the cross refinement has exactly the same elements
	 * @param newAt
	 * @param baseE
	 * @return
	 */
	private boolean isEqualRefinement(ActionTransformerModel newAt,	ActionTransformerModel baseE) {

		if(!newAt.getInnerRefinementType().equals(baseE.getInnerRefinementType())){
			return false;
		}
		int newInnerRefSize = newAt.getInnerRefinements().size();
		int baseInnerRefSize = baseE.getInnerRefinements().size();
		if(newInnerRefSize != baseInnerRefSize)
			return false;
		int newCrossRefSize = newAt.getCrossRefinements().size();
		int baseCrossRefSize = baseE.getCrossRefinements().size();
		if(newCrossRefSize != baseCrossRefSize)
			return false;
		
		if(newCrossRefSize == 0){
			//only inner refined.
			if(newInnerRefSize == 0){
				//final element
				return false;
			}
		}
		
		ArrayList<ActionTransformerModel> newInnerRef = newAt.getInnerRefinements();
		ArrayList<ActionTransformerModel> baseInnerRef = baseE.getInnerRefinements();
		boolean isSequence = newAt.getInnerRefinementType().equals(RefinementType.SEQ);
		for(int i=0; i<newInnerRef.size(); i++){
			ActionTransformerModel newRef = newInnerRef.get(i);
			ActionTransformerModel baseRef = null;
			if(isSequence){
				baseRef = baseInnerRef.get(i);
				if(!baseRef.equals(newRef))
					return false;
			}
			else{
				baseRef = baseE.getInnerRefinement(newRef);
				if(baseRef == null)
					return false;
			}
		}
		
		ArrayList<ActionTransformerModel> newCrossRef = newAt.getCrossRefinements();
		for(int i=0; i<newCrossRef.size(); i++){
			ActionTransformerModel newRef = newCrossRef.get(i);
			ActionTransformerModel baseRef = null;
			baseRef = baseE.getCrossRefinement(newRef);
				if(baseRef == null)
					return false;
		}
		
		return true;
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
			if(!newE.equivalent(baseE))
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
		ArrayList<ActionTransformerModel> seq = at.getInnerRefinements();
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
		ArrayList<ActionTransformerModel> newSeq = newAt.getInnerRefinements();
		ArrayList<ActionTransformerModel> baseSeq = baseAt.getInnerRefinements();		
		
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
