package de.tum.in.i22.uc.ptp.adaptation.engine;

import java.util.ArrayList;

<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/adaptation/engine/DataContainerAdaptationController.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.ptp.adaptation.model.DomainModel;
import de.tum.in.i22.uc.ptp.adaptation.model.LayerModel;
=======
import de.tum.in.i22.uc.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.utilities.PtpLogger;
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/adaptation/engine/DataContainerAdaptationController.java

/**
 * @author Cipri
 *
 *- for containers at PSM and ISM level the system is not important. it does not affect the merging
 *- for PSM and ISM there is no relation between the words in terms of semantics.
	At PSM and ISM level the containers describe standard names containers.
	These names should be added only by a power user who knows the specific APIs of the systems.
	Assumption:	We consider at PSM the terminology for containers and transformers to have a standard for the systems. 
	<p>
	For example: - there should not be for OS system a container named file and in another domain named file1 if they describe the same thing
	File remains file for OS systems. There are no two ontologies with an OS system in which in one case there is File1 and in the other one there is File2. 
	The differences are at PIM and ISM (e.g. Linux vs Windows), but at PSM, for the same system, I should expect to use the same terms. 
	At ISM if the names differ it means that there are two different functions. Same assumptions should applies at PSM.	This is a reasonable assumption.
 *
 *
 */
public class DataContainerAdaptationController {

	private PtpLogger logger ;
	
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
	
	public DataContainerAdaptationController(DomainModel baseDM, DomainModel newDM){
		this.baseDm = baseDM;
		this.newDm = newDM;
		this.wordnetEngine = WordnetEngine.getInstance();
		this.logger = PtpLogger.adaptationLoggerInstance();
	}
	
	/**
	 * BOTTOM-UP approach	
	 * STEP 0: merge domain models: Data and Containers
	 * @throws DomainMergeException
	 */
	public void mergeDomainModels() {
		updatedElementsCounter = 0;
		// see STEP 1
		mergeDCAssociations(baseDm, newDm);	
		// see STEP 2
		mergeDCRefinements(baseDm, newDm);
		// see STEP 3
		mergeData(baseDm, newDm);
		
		baseDm.getIsmLayer().filterDataContainerRefinements();
		baseDm.getPsmLayer().filterDataContainerRefinements();
		baseDm.getPimLayer().filterDataContainerRefinements();
	}

	/**
	 * ---------------------------------------------------------------------------
	 * <br>
	 *  STEP 1: merge PSM, ISM container associations / inner links
	 *  <br>
	 *	---------------------------------------------------------------------------
	 * <br>
	 *	New Containers are added to the BaseDomainModel.
	 *	New Associations between Containers are added to the BaseDomainModel.
	 *	The comparison is only a string matching for Containers. (see assumption)
	 *	<p>
	 *	At this step only the inner refinement, associations (inner links) are merged.
	 *	If the new model defines new containers, these containers are added.
	 *	If the new model defines new associations at the same level for a container, these associations are added.
	 *	<p>
	 *	For example:
	 *	baseDomain: domElement is associated with img only
	 *	newDomain:	domElement is associated with html, media
	 *				media is associated with img and video
	 *	After this step is finished the baseDomain will be the following
	 *	baseDomain:	domElement is associated with html, media, img
     *
	 * @param baseDm2
	 * @param newDm2
	 */
	private void mergeDCAssociations(DomainModel baseDm, DomainModel newDm) {
		mergeDCLayer(baseDm.getIsmLayer(), newDm.getIsmLayer());
		mergeDCLayer(baseDm.getPsmLayer(), newDm.getPsmLayer());
	}
	
	/**
	 * Add new Data/Container to the Base Domain layer
	 * @param psmLayer
	 * @param psmLayer2
	 */
	private void mergeDCLayer(LayerModel baseLayer, LayerModel newLayer) {
		ArrayList<DataContainerModel> newContainers = newLayer.getDataContainers();
		for(DataContainerModel newDc : newContainers){
			addNewContainerToBase(newDc, baseLayer);
		}
	}

	private void addNewContainerToBase(DataContainerModel newDc, LayerModel baseLayer) {
		if(newDc.isMerged())
			return;
		
		//add associations elements		
		ArrayList<DataContainerModel> newAssoc = newDc.getAssociations();
		for(DataContainerModel assoc : newAssoc){
			addNewContainerToBase(assoc, baseLayer);
		}
	
		//at this step only simple(no associations) system containers are considered
		//container checked based on name equality
		DataContainerModel existsDC = null;
		ArrayList<DataContainerModel> baseLayerContainers = baseLayer.getDataContainers();
		for(DataContainerModel baseDc : baseLayerContainers){
			boolean relationSimilar = isDataContainerSimilar(newDc, baseDc);
			if(relationSimilar){
				existsDC = baseDc;
				break;
			}
		}
		
		if(existsDC == null){
			existsDC = new DataContainerModel(newDc.getName(), newDc.getLayerType());
			existsDC.setParenLayer(baseLayer);
			baseLayer.addDataContainer(existsDC);
			incrementUpdateCounter();
		}

		//add associations links
		for(DataContainerModel assoc : newAssoc){
			DataContainerModel link = existsDC.getAssociationLink(assoc.getName());
			if(link == null){
				DataContainerModel toAdd = baseLayer.getDataContainer(assoc.getName());
				//adds the new link
				existsDC.addAssociation(toAdd);
				incrementUpdateCounter();
			}
		}
		
		/* mark as merge to optimize the search of the domain model */
		newDc.markAsMerged();
	}

	/**
	 * PIM: <br>
	 * PSM: container checked based on name equality
	 * <br>
	 * ISM: container checked based on name equality
	 * @param newDc
	 * @param baseDc
	 * @return
	 */
	private boolean isDataContainerSimilar(DataContainerModel newDc, DataContainerModel baseDc) {
		//TODO: lower case everything? - name check
		String newName = newDc.getName();
		String baseName = baseDc.getName();
		if(newName.equals(baseName)){
			return true;
		}
		
		// check the newName in the synonyms of the base
		if(baseDc.alsoKnownAs(newName)){
			return true;
		}
		// check the baseName in the synonyms of the new
		if(newDc.alsoKnownAs(baseName)){
			return true;
		}
		
		return false;
	}

	/**
	 * ---------------------------------------------------------------------------
	 *	STEP 2: merge PSM, ISM container implementations
	 *	---------------------------------------------------------------------------
	 *	Documentation:
	 *	All Containers are now present in the BaseModel.
	 *	All Associations are now present in the BaseModel.
	 *	In this step new cross refinements are added for Containers.
	 *	The comparison is only a string matching for Containers. (see assumption)
	 *	
	 *	?? are there ISM inner implementations? 
	 *	---------------------------------------------------------------------------
	 * 
	 * @param baseDm2
	 * @param newDm2
	 */
	private void mergeDCRefinements(DomainModel baseDm, DomainModel newDm) {
		mergeDCLayerRefinements(baseDm.getIsmLayer(), newDm.getIsmLayer());
		mergeDCLayerRefinements(baseDm.getPsmLayer(), newDm.getPsmLayer());
	}
	
	private void mergeDCLayerRefinements(LayerModel baseLayer, LayerModel newLayer) {
		for(DataContainerModel newDc : newLayer.getDataContainers()){
			DataContainerModel baseDc = baseLayer.getDataContainer(newDc.getName());
			addNewDcRefinement(baseDc, newDc);
		}
	}

	/**
	 * Add newDc as a refinement to baseDc
	 * @param baseDc - Base Data Container
	 * @param newDc - New Data Container
	 */
	private void addNewDcRefinement(DataContainerModel baseDc,	DataContainerModel newDc) {
		for(DataContainerModel newRef : newDc.getRefinements()){
			DataContainerModel baseDcRef = baseDc.getRefinementByName(newRef.getName());
			if(baseDcRef == null){
				if(newRef.getLayerType().equals(baseDc.getLayerType())){
					// refinement at the same level
					baseDcRef = baseDm.getLayer(baseDc.getLayerType()).getDataContainer(newRef.getName());
				}
				else {
					baseDcRef = baseDm.getLayer(baseDc.getLayerType()).getRefinementLayer().getDataContainer(newRef.getName());
				}
				baseDc.addRefinement(baseDcRef);
				incrementUpdateCounter();
			}
		}
	}

	/**
	 * 
	 * -------------------------
	 *	STEP 3: merge PIM data
	 *	------------------------
	 *
	 *	Documentation:
	 *	PSM and ISM are now merged.
	 *	At this step the PIM Data of the BaseDomainModel and NewDomainModel are compared.
	 *	
	 *	relationRatioMax = LOWER_LIMIT;
	 *	LOWER_LIMIT - represents the maximum distance allowed between two terms
	 *	e.g. picture and photo 
	 *	- this value can be achieved with a WordNet library
	 *	
	 *	There are no merging exceptions for DATA.
	 *	---------------------------------------------------------------------------
	 * 
	 * @param baseDm2
	 * @param newDm2
	 */
	private void mergeData(DomainModel baseDm, DomainModel newDm) {
		LayerModel basePIM = baseDm.getPimLayer();
		LayerModel newPIM = newDm.getPimLayer();
		for(DataContainerModel newDc : newPIM.getDataContainers()){
			mergePimData(newDc, basePIM);
		}
	}

	/**
	 * Add newDc to basePIM by merging the new data.
	 * @param newDc - new Data
	 * @param basePIM - Base Domain Model
	 */
	private void mergePimData(DataContainerModel newDc, LayerModel basePIM) {
		
		if(newDc.isMerged())
			return;
		
		for(DataContainerModel newAggreg : newDc.getAggregations()){
			mergePimData(newAggreg, basePIM);
		}
		for(DataContainerModel newComp : newDc.getCompositions()){
			mergePimData(newComp, basePIM);
		}
		
		//at this step only simple(no associations) data are considered
		//data checked based on name relation
		DataContainerModel existsDC = null;
		float relationRatioMax = WordnetEngine.MAX_ALLOWED_DISTANCE; 

		//to optimize search for string matching first. 
		
		//you avoid conflicts of names in case of renaming.
		
		//1. find the closest match - string
		for(DataContainerModel baseDc : basePIM.getDataContainers()){
			//check first if the names are equal or there is a match between the aliases
			boolean similarContainer = isDataContainerSimilar(newDc, baseDc);
			if(similarContainer){
				//do nothing - container already exists in the base
				existsDC = baseDc;					
				break;
			}
		}
		
		if(existsDC == null){
			//2. find the closest match - wordnet
			float relationRatio = relationRatioMax;
			for(DataContainerModel baseDc : basePIM.getDataContainers()){
				//try to find some similarity between the concepts.
				relationRatio = wordnetEngine.getDistance(newDc.getName(), baseDc.getName());
				//search in the aliases only if there was no direct match between the names
				if(relationRatio > WordnetEngine.EQUAL_DISTANCE){
					float relationRatioAliasBase = wordnetEngine.getBestDistance(newDc.getName(), baseDc.getSynonyms());
					float relationRatioAliasNew = wordnetEngine.getBestDistance(baseDc.getName(), newDc.getSynonyms());
					if(relationRatioAliasBase < relationRatio)
						relationRatio = relationRatioAliasBase;
					if(relationRatioAliasNew < relationRatio)
						relationRatio = relationRatioAliasBase;
				}
				
				if(relationRatio == WordnetEngine.EQUAL_DISTANCE){
					//do nothing - container already exists in the base
					existsDC = baseDc;					
					relationRatioMax = relationRatio;
					break;
				} else if (relationRatio < WordnetEngine.SIMILAR_NOUN_MAX_DISTANCE){
					//do nothing - container already exists in the base
					if(relationRatio < relationRatioMax){
						existsDC = baseDc;
						relationRatioMax = relationRatio;
					}
				}else {
					if(relationRatio < relationRatioMax){
						relationRatioMax = relationRatio;
					}
				}
			}
		}
		
		if(existsDC == null){
			//no relation was established. this is a new type of DATA
			existsDC = new DataContainerModel(newDc.getName(), newDc.getLayerType());
			existsDC.setParenLayer(basePIM);
			for(String newAlias : newDc.getSynonyms()){
				existsDC.addSynonym(newAlias);
			}
			basePIM.addDataContainer(existsDC);
			incrementUpdateCounter();
		}
		else {
			if(!existsDC.getName().equals(newDc.getName())){
				existsDC.addSynonym(newDc.getName());
				//rename for the newDC to optimize the search
				String newName = newDc.getName();
				newDc.setName(existsDC.getName());
				newDc.addSynonym(newName);
				String logMsg = "similiratity found: (base-new) " + existsDC.getName() +"-"+ newName;
				logger.infoLog(logMsg, null);
			}
				
			for(String alias : newDc.getSynonyms()){
				existsDC.addSynonym(alias);
				incrementUpdateCounter();
			}
		}
		
		//add refinement links
		for(DataContainerModel ref : newDc.getRefinements()){
			DataContainerModel baseRef = existsDC.getRefinementByName(ref.getName());
			if(baseRef == null){
				DataContainerModel toAdd = basePIM.getRefinementLayer().getDataContainer(ref.getName());
				existsDC.addRefinement(toAdd);
				incrementUpdateCounter();
			}
		}
		
		//add associations links
		for(DataContainerModel newAggreg : newDc.getAggregations()){
			DataContainerModel link = existsDC.getAggregationLink(newAggreg.getName());
			if(link == null){
				DataContainerModel toAdd = basePIM.getDataContainer(newAggreg.getName());
				existsDC.addAggregation(toAdd);
				incrementUpdateCounter();
			}
		}
		
		for(DataContainerModel newComp : newDc.getCompositions()){
			DataContainerModel link = existsDC.getCompositionLink(newComp.getName());
			if(link == null){
				DataContainerModel toAdd = basePIM.getDataContainer(newComp.getName());
				existsDC.addComposition(toAdd);
				incrementUpdateCounter();
			}
		}
		
		newDc.markAsMerged();
		
	}

	

	
	
}
