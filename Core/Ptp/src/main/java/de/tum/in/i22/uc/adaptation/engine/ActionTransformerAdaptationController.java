package de.tum.in.i22.uc.adaptation.engine;

import java.util.ArrayList;

import org.apache.commons.codec.language.RefinedSoundex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel;
import de.tum.in.i22.uc.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel.RefinementType;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;
/**
 * @author Cipri
 *
 */
public class ActionTransformerAdaptationController {

	private static int sequenceCounter = 0;
	private static final Logger logger = LoggerFactory.getLogger(ActionTransformerAdaptationController.class);
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
		
		// see STAGE 1
		mergeInnerLinks(baseDm, newDm);
		// see STAGE 2
		mergeCrossLinks(baseDm, newDm);
		
		// see STAGE 3
		baseDm.getIsmLayer().filterActionTransformerSequences();
		baseDm.getPsmLayer().filterActionTransformerSequences();
		baseDm.getPimLayer().filterActionTransformerSequences();
	}

	

	private void mergeInnerLinks(DomainModel baseDm, DomainModel newDm) throws DomainMergeException {		
		mergeInnerLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		mergeCrossLinksLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		
		mergeInnerLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		mergeCrossLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		
		mergeInnerLinksLayer(baseDm.getPimLayer(), newDm.getPimLayer());
		mergeCrossLinksLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
		
	}
	
	private void mergeCrossLinksLayer(LayerModel baseLayer, LayerModel newLayer) {
		ArrayList<ActionTransformerModel> newActionTransformers = newLayer.getActionTransformers();
		for(ActionTransformerModel newAT : newActionTransformers){
			addNewTransformerRefinement(newAT, baseLayer);
		}
		
	}

	private void addNewTransformerRefinement(ActionTransformerModel newAT, LayerModel baseLayer) {
		
	}

	private void mergeInnerLinksLayer(LayerModel baseLayer, LayerModel newLayer) throws DomainMergeException{
		ArrayList<ActionTransformerModel> newActionTransformers = newLayer.getActionTransformers();
		for(ActionTransformerModel newAT : newActionTransformers){
			addNewTransformerToBase(newAT, baseLayer);
		}
	}

	
	private void addNewTransformerToBase(ActionTransformerModel newAt,	LayerModel baseLayer) throws DomainMergeException {
		if(newAt.isMerged())
			return;
		
		//if the refinement is at a different level, then return
		if(!newAt.getLayerType().equals(baseLayer.getType()))
			return;
		
		for(ActionTransformerModel innerLink : newAt.getRefinements()){
			addNewTransformerToBase(innerLink, baseLayer);
		}
		
		ActionTransformerModel baseAt = searchEquivalent(newAt, baseLayer);
		if(baseAt == null){
			baseAt = new ActionTransformerModel(newAt.getName(), newAt.getLayerType());
			baseAt.setParenLayer(baseLayer);
			/* The parent system will be updated when the systems are merged.
			 * Each transformer must have a parent system.
			 */
			baseAt.setParentSystem(newAt.getParentSystem());
			baseLayer.addActionTransformer(baseAt);
		}
		
		if(newAt.getRefinementType().equals(RefinementType.SET)){
			if(baseAt.getRefinementType().equals(RefinementType.SET)){
				updateInputAndOutputContainers(newAt, baseAt);
				
				if(baseLayer.getType().equals(LayerType.PIM)){
					updateSynonyms(newAt, baseAt);
				}
			}
		}
		
		newAt.markAsMerged();
		
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
		}
	}
	
	private void updateInputAndOutputContainers(ActionTransformerModel newAt, ActionTransformerModel baseAt){
		//input params
		for(DataContainerModel dc : newAt.getInputParams()){
			baseAt.addInputParam(dc);
		}
		//output
		for(DataContainerModel dc : newAt.getOutputParams()){
			baseAt.addOutputParam(dc);
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
		
		if(newAt.getParentSystem()!=null)
			throw new DomainMergeException("Please define a parent system for element: "+ newAt.toString());
		
		for(ActionTransformerModel baseE : baseLayer.getActionTransformers()){
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
		 * If we don't associate a sequence with a particular system, 
		 * then we don't know which transformer from which system should we update.
		 * It is unreasonable to apply the same sequence to all of them.
		 * The solution is to specify a system also for sequences.
		 * e.g. copyFile - for OS, copyFile - for VM.
		 * according to the definition these two are different because they pertain to different systems.
		 * if new domain model comes with a sequence CopyFile, than the system does not know which 
		 * transformer to update.   
		 */
			
			if(baseE.getParentSystem()==null)
				throw new DomainMergeException("Please define a parent system for element: "+ baseE.toString());
			
			if(baseE.getParentSystem().equals(newAt.getParentSystem())){
				if(nameCheck){
					baseEquivalent = baseE;
					break;
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
			} else if (relationRatio < WordnetEngine.VERY_SIMILAR_DISTANCE){
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
	

	private void mergeCrossLinks(DomainModel baseDm2, DomainModel newDm2) {
		
	}
}
