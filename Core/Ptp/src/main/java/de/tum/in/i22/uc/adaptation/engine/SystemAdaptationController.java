package de.tum.in.i22.uc.adaptation.engine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.adaptation.model.SystemModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

public class SystemAdaptationController {

	/**
	 * The Base Domain Model.
	 * The results of the adaptation will be stored here.
	 */
	private DomainModel baseDm;
	
	private static final Logger logger = LoggerFactory.getLogger(SystemAdaptationController.class);
	
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
	 * The New Domain Model which possibly contains new definitions. 
	 */
	private DomainModel newDm;
	
	public SystemAdaptationController(DomainModel baseDomainModel, DomainModel newDomainModel) {
		this.baseDm = baseDomainModel;
		this.newDm = newDomainModel;
	}
	
	/**
	 * BOTTOM-UP approach	
	 * STEP 0: merge domain models: Systems
	 * @throws DomainMergeException
	 */
	public void mergeDomainModels() throws DomainMergeException{
		
		mergeLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		
		mergeLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
	}


	private void mergeLayer(LayerModel baseLayer, LayerModel newLayer) {
		ArrayList<SystemModel> systems = newLayer.getSystems();
		for(SystemModel newS : systems){
			addNewSystemToBase(newS, baseLayer);
		}
		
		baseLayer.filterSystemRefinements();
	}
	
	/**
	 * See ./doc/ paper - Algorithm2
	 * @param newS
	 * @param baseLayer
	 * @throws DomainMergeException
	 */
	private void addNewSystemToBase(SystemModel newSys, LayerModel baseLayer) {
		if(newSys.isMerged())
			return;
		
		//if the element is at a different level, then return
		if(!newSys.getLayerType().equals(baseLayer.getType()))
			return;
		
		for(SystemModel innerLink : newSys.getAssociations()){
			addNewSystemToBase(innerLink, baseLayer);
		}
		
		logger.debug("Try ADD NewSystem: "+ newSys.toStringShort()+ " To Base: "+ baseLayer.getType());
		SystemModel baseSys = searchEquivalent(newSys, baseLayer);
		if(baseSys == null){
			logger.debug("created NewSystem: " + newSys.toStringShort());
			baseSys = new SystemModel(newSys.getName(), newSys.getLayerType());
			baseSys.setParenLayer(baseLayer);
			/* The parent system will be updated when the systems are merged.
			 * Each transformer must have a parent system.
			 */
			//baseAt.setParentSystem(newAt.getParentSystem());
			baseLayer.addSystem(newSys);		
			incrementUpdateCounter();
		}
		else{
			logger.debug("Equivalent found: base "+ newSys.toStringShort());
		}
		
		mergeRefinement(newSys, baseSys);
		
		updateAssociations(newSys, baseSys);
		
		updateSystemForTransformers(baseSys, baseLayer);
		
		newSys.markAsMerged();
		logger.debug("ADD Success: "+ newSys.toString());
		
	}

	
	private void updateSystemForTransformers(SystemModel baseSys, LayerModel baseLayer) {
		for(ActionTransformerModel at : baseLayer.getActionTransformers()){
			if(at.getParentSystem().equals(baseSys)){
				/* this seems redundant but it is not.
				 * the systems are compared only by name, not by object.
				 * when you added a transformer from a new system, 
				 * the system object remained from the new model.
				 * now you update that object. */
				at.setParentSystem(baseSys);
			}
		}
		
	}

	/**
	 * See doc/metamodel.png
	 * <br> PIM - no system element 
	 * <br> PSM - systemassociation attribute
	 * <br> ISM - implesystemassociation attribute
	 * @param newAt
	 * @param baseAt
	 */
	private void updateAssociations(SystemModel newSys, SystemModel baseSys){
		for(SystemModel assoc : newSys.getAssociations()){
			SystemModel baseAssoc = newSys.getAssociationLink(assoc);
			if(baseAssoc == null){
				baseAssoc = baseDm.getLayer(baseSys.getLayerType()).getSystem(assoc);
				baseSys.addAssociationLink(baseAssoc);
				incrementUpdateCounter();
				logger.debug("UPDATE association: base "+ baseSys.toString() + " EXTENDED with "+ baseAssoc);
			}
		}
	}

	/**
	 * All the refinement elements of the newSys are added to the refinement of the baseSys. 
	 * @param newSys.SET refinement
	 * @param baseSys.SET refinement
	 */
	private void mergeRefinement(SystemModel newSys,	SystemModel baseSys) {
		logger.debug("Merge Set2Set - new: "+ newSys.toStringShort() +" base: "+ baseSys.toStringShort());
		for(SystemModel ref : newSys.getRefinements()){
			SystemModel baseRef = baseSys.getRefinement(ref);
			if(baseRef == null){
				if(ref.getLayerType().equals(baseSys.getLayerType())){
					LayerModel refLayer = baseDm.getLayer(baseSys.getLayerType());
					baseRef = refLayer.getSystem(ref);					
				}
				else{
					LayerModel refLayer = baseDm.getLayer(baseSys.getLayerType()).getRefinementLayer();
					baseRef = refLayer.getSystem(ref);
				}				
				logger.debug("Merge Set2Set EXTENDED base "+ baseSys.toStringShort() + "  WITH "+ baseRef.toStringShort());
				baseSys.addRefinement(baseRef);
				incrementUpdateCounter();
			}			
		}
	}
	
	/**
	 * Implements the equivalence relation between two systems.
	 * @param newSys
	 * @param baseLayer
	 * @return baseSystem equivalent from base 
	 * <br> null if not found
	 */
	private SystemModel searchEquivalent(SystemModel newSys, LayerModel baseLayer) {
		for(SystemModel baseE : baseLayer.getSystems()){
			logger.debug("Search equivalent - base: " + baseE.toStringShort() + " new: "+ newSys.toStringShort());
			//a redundant safety type check
			if(!baseE.getLayerType().equals(newSys.getLayerType()))
				continue;
			
			if(baseE.getName().equals(newSys.getName()))
				return baseE;
		}
		// if nothing was found, the result is null
		return null;
	}
	
}
