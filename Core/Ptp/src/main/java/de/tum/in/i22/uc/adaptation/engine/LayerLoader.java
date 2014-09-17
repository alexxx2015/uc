package de.tum.in.i22.uc.adaptation.engine;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel;
import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel.RefinementType;
import de.tum.in.i22.uc.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.adaptation.model.SystemModel;

/**
 * @author Cipri
 * This class creates the domain model objects
 * by extracting the data specified in the xml domain model.
 */
public class LayerLoader {
	
	private LayerModel layer ;
	private static final Logger logger = LoggerFactory.getLogger(LayerLoader.class);
	
	public LayerLoader (LayerModel layer){
		this.layer = layer;
	}
	
	/**
	 * Process the list of containers.
	 * Add them to the corresponding layer.
	 * @param containers
	 */
	public void addContainers (NodeList containers){
		if(containers == null)
			return;
		int containersCounter = containers.getLength();
		/*use this map for the inner refinement*/
		HashMap<DataContainerModel, Node> containerNodesMap = new HashMap<DataContainerModel,Node>();
		for(int i=0; i<containersCounter; i++){
			Node node = containers.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			logger.info(layer.getType()+" Loading data/container: "+ name);
			DataContainerModel container = new DataContainerModel(name, layer.getType());
			container.setParenLayer(layer);
			addPimContainerSynonyms(container, node);

			containerNodesMap.put(container, node);
			layer.addDataContainer(container);
		}
		
		/* do the inner refinement */
		for(DataContainerModel dc : containerNodesMap.keySet()){
			Node node = containerNodesMap.get(dc);

			switch(layer.getType()){
			case PIM:				
				addPimContainerInnerRefinement(dc, node);
				addPimContainerCrossRefinement(dc, node);
				break;
			case PSM:
				addPsmContainerInnerRefinement(dc, node);
				addPsmContainerCrossRefinement(dc, node);
				break;
			case ISM:
				addIsmContainerInnerRefinement(dc, node);
				addIsmContainerCrossRefinement(dc, node);
			default:
				break;
			}
		}
		
	}

	/**
	 * Process the list of PIM actions or PSM, ISM transformers.
	 * Add them to the corresponding layer.
	 * @param transformers
	 */
	public void addTransformers(NodeList transformers) {
		if(transformers == null)
			return;
		int transformersCounter = transformers.getLength();
		/*use this map for the inner refinement*/
		HashMap<ActionTransformerModel, Node> transformersNodesMap = new HashMap<ActionTransformerModel,Node>();
		for(int i=0; i<transformersCounter; i++){
			Node node = transformers.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			logger.debug(layer.getType()+" Loading action/transformer: "+ name);
			RefinementType refType = RefinementType.SET;
			try{
				String refTypeAttr = "";
				refTypeAttr = node.getAttributes().getNamedItem("refType").getNodeValue();
				if(refTypeAttr.equals("seqRefmnt"))
					refType = RefinementType.SEQ;
			}catch(Exception ex){}
			
			ActionTransformerModel actionTransformer = new ActionTransformerModel(name, layer.getType());
			actionTransformer.setParenLayer(layer);
			actionTransformer.setRefinementType(refType);
			transformersNodesMap.put(actionTransformer, node);
			
			
			switch(layer.getType()){
				case PIM:
					addPimActionSynonyms(actionTransformer, node);
					addPimActionParamData(actionTransformer, node);
					break;
				case PSM:
					addPsmTransformerInputContainers(actionTransformer, node);
					addPsmTransformerOutputContainers(actionTransformer, node);
					break;
				case ISM:
					addIsmTransformerInputContainers(actionTransformer, node);
					addIsmTransformerOutputContainers(actionTransformer, node);
				default:
					break;
			}
			layer.addActionTransformer(actionTransformer);
		}
		
		/* do the inner refinement and cross*/
		for(ActionTransformerModel at : transformersNodesMap.keySet()){
			Node node = transformersNodesMap.get(at);
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			logger.debug(layer.getType()+" inner/cross ref :"+ name);
			switch(layer.getType()){
			case PIM:
				addPimActionInnerAssociation(at, node);
				addPimActionCrossRefinement(at, node);
				break;
			case PSM:
				addPsmTransformerInnerRefinement(at, node);
				addPsmTransformerCrossRefinement(at, node);
				break;
			case ISM:
				addIsmTransformerInnerRefinement(at, node);
				addIsmTransformerCrossRefinement(at, node);
			default:
				break;
			}
		}
		
	}
	
	/**
	 * Process the list of PSM or ISM systems.
	 * Add them to the corresponding layer.
	 * @param systems
	 */
	public void addSystems(NodeList systems) {
		if(systems == null)
			return;
		int systemsCounter = systems.getLength();
		/*use this map for the inner associations*/
		HashMap<SystemModel, Node> systemNodesMap = new HashMap<SystemModel,Node>();
		for(int i=0; i<systemsCounter; i++){
			Node node = systems.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			logger.debug(layer.getType()+" Loading system: "+ name);
			SystemModel system = new SystemModel(name, layer.getType());
			system.setParenLayer(layer);
			systemNodesMap.put(system, node);
			
			switch(layer.getType()){
				case PIM:
					break;
				case PSM:
					addPsmSystemTransformers(system,node);
					break;
				case ISM: 
					addIsmSystemTransformers(system,node);
				default:
					break;
			}
			
			layer.addSystem(system);
		}
		
		/* do the inner refinement and cross*/
		for(SystemModel sys : systemNodesMap.keySet()){
			Node node = systemNodesMap.get(sys);
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			logger.debug(layer.getType()+" inner/cross ref :"+ name);
			switch(layer.getType()){
			case PIM:
				break;
			case PSM:
				addPsmSystemInnerAssociation(sys, node);
				addPsmSystemCrossRefinement(sys, node);
				break;
			case ISM:
				addIsmSystemInnerAssociation(sys, node);
				addIsmSystemCrossRefinement(sys, node);
			default:
				break;
			}
		}
		
	}
	

	/**************************************************************
	 * PIM level operations
	 * */
	
	/**
	 * Adds the synonyms from the XML to the Data object.
	 * The synonyms are specified in an attribute "synonym" separated by space.
	 * @param pimData - PIM Data object
	 * @param node - PIM Data XML element
	 */
	private void addPimActionSynonyms(ActionTransformerModel action, Node node) {
		Node pimSynonyms = node.getAttributes().getNamedItem("synonym");
		if(pimSynonyms == null)
			return;
		String synonymsAsData = pimSynonyms.getNodeValue();
		String[] synonyms = synonymsAsData.split(" ");
		for (String syn : synonyms) {
			action.addSynonym(syn);
		}
	}
	
	/** Adds the data parameters of the XML action to the object.
	 *  
	 * @param action - the PIM action object element
	 * @param node - the PIM action XML element
	 */
	private void addPimActionParamData(ActionTransformerModel action, Node node) {
		Node pimParamData = node.getAttributes().getNamedItem("paramData");
		if(pimParamData == null)
			return;
		String pimParamDataValues = pimParamData.getNodeValue();
		String[] params = pimParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@pims/@pimdata.", "");
			int position = Integer.parseInt(element);
			DataContainerModel pData = layer.getContainerAtPosition(position);
			action.addInputParam(pData);
		}
	}
	
	private void addPimActionCrossRefinement(ActionTransformerModel pimAction, Node node){
		Node pimActionRefinement = node.getAttributes().getNamedItem("actionRefmnt");
		if(pimActionRefinement == null)
			return;
		String implementedAsData = pimActionRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			if(element.contains("//@pims/@pimactions.")){
				//refinement at the same layer
				element = element.replace("//@pims/@pimactions.", "");
				int position = Integer.parseInt(element);
				ActionTransformerModel refinement = layer.getTransformerAtPosition(position);
				pimAction.addRefinement(refinement);
			}
			else if(element.contains("//@psms/@psmtransformers.")){
				element = element.replace("//@psms/@psmtransformers.", "");
				int position = Integer.parseInt(element);
				ActionTransformerModel refinement = layer.getRefinementLayer().getTransformerAtPosition(position);
				pimAction.addRefinement(refinement);
			}
		}
	}
	
	/**
	 * Adds the synonyms from the XML to the Data object.
	 * The synonyms are specified in an attribute "synonym" separated by space.
	 * @param pimData - PIM Data object
	 * @param node - PIM Data XML element
	 */
	private void addPimContainerSynonyms(DataContainerModel pimData, Node node) {
		Node pimSynonyms = node.getAttributes().getNamedItem("synonym");
		if(pimSynonyms == null)
			return;
		String synonymsAsData = pimSynonyms.getNodeValue();
		String[] synonyms = synonymsAsData.split(" ");
		for (String syn : synonyms) {
			pimData.addSynonym(syn);
		}
	}
	
	/**
	 * @param pimData - PIM Data object
	 * @param node - PIM Data XML element
	 */
	private void addPimContainerCrossRefinement(DataContainerModel pimData, Node node){
		Node pimStoredIn = node.getAttributes().getNamedItem("storedin");
		if(pimStoredIn == null)
			return;
		String implementedAsData = pimStoredIn.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			if(element.contains("//@pims/@pimdata.")){
				element = element.replace("//@pims/@pimdata.", "");
				int position = Integer.parseInt(element);
				DataContainerModel refinement = layer.getContainerAtPosition(position);
				pimData.addRefinement(refinement);
			}
			else if (element.contains("//@psms/@psmcontainers.")){
				element = element.replace("//@psms/@psmcontainers.", "");
				int position = Integer.parseInt(element);
				DataContainerModel refinement = layer.getRefinementLayer().getContainerAtPosition(position);
				pimData.addRefinement(refinement);
			}
		}
	}
	
	private void addPimActionInnerAssociation(ActionTransformerModel pimAction, Node node){
		Node pimActionAssociation = node.getAttributes().getNamedItem("actionassociation");
		if(pimActionAssociation == null)
			return;
		//TODO: I don't know how the association for the actions looks like. Is it a child node or an attribute?
		String associationData = pimActionAssociation.getNodeValue();
		String[] associations = associationData.split(" ");
		for(int i =0; i<associations.length; i++){
			String element = associations[i].trim();
			element = element.replace("//@pims/@pimactions.", "");
			int position = Integer.parseInt(element);
			ActionTransformerModel association = layer.getTransformerAtPosition(position);
			pimAction.addAssociationLink(association);
		}
	}
	
	/**
	 * Inner Links 
	 * @param pimContainer
	 * @param node
	 */
	private void addPimContainerInnerRefinement(DataContainerModel pimContainer, Node node){
		//applies at PIM
		NodeList children = node.getChildNodes();
		if(children == null)
			return;
		
		int childrenCounter = children.getLength();
		for(int i=0; i<childrenCounter; i++){
			Node child = children.item(i);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			String nodeType = child.getNodeName();
			String associationType = "";
			String targetAssoction = "";
			try{
				associationType = child.getAttributes().getNamedItem("assoType").getNodeValue();
				targetAssoction = child.getAttributes().getNamedItem("targetAssoData").getNodeValue();
			}catch(Exception ex){
				continue;
			}
			
			if(associationType.equals("isAggregationOf")){
				String element = targetAssoction.trim();
				element = element.replace("//@pims/@pimdata.", "");
				int position = Integer.parseInt(element);
				DataContainerModel association = layer.getContainerAtPosition(position);
				pimContainer.addAggregation(association);
			}
			else if(associationType.equals("isCompositionOf")){
				String element = targetAssoction.trim();
				element = element.replace("//@pims/@pimdata.", "");
				int position = Integer.parseInt(element);
				DataContainerModel association = layer.getContainerAtPosition(position);
				pimContainer.addComposition(association);
			}
			
		}
		
	}
	
	/**************************************************************
	 * PSM level 
	 * */
	
	/**
	 * See doc/metamodel.png: PSM Container element - containerassociation attribute
	 * @param psmContainer
	 * @param node
	 */
	private void addPsmContainerInnerRefinement(DataContainerModel psmContainer, Node node){
		//applies at PSM
		Node psmAssociation = node.getAttributes().getNamedItem("containersassociation");
		if(psmAssociation == null)
			return;
		String associationData = psmAssociation.getNodeValue();
		String[] associations = associationData.split(" ");
		for(int i =0; i<associations.length; i++){
			String element = associations[i].trim();
			element = element.replace("//@psms/@psmcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel association = layer.getContainerAtPosition(position);
			psmContainer.addAssociation(association);
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM Container element - contimplementedas attribute
	 * @param psmContainer
	 * @param node
	 */
	private void addPsmContainerCrossRefinement(DataContainerModel psmContainer, Node node){
		//applies at PSM
		Node psmImplementedAs = node.getAttributes().getNamedItem("contimplementedas");
		if(psmImplementedAs == null)
			return;
		String implementedAsData = psmImplementedAs.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			if(element.contains("//@isms/@ismcontainers.")){
				element = element.replace("//@isms/@ismcontainers.", "");
				int position = Integer.parseInt(element);
				DataContainerModel refinement = layer.getRefinementLayer().getContainerAtPosition(position);
				psmContainer.addRefinement(refinement);
			}
			else if(element.contains("//@psms/@psmcontainers.")){
				element = element.replace("//@psms/@psmcontainers.", ""); //for the case when we have inner refinement
				int position = Integer.parseInt(element);
				DataContainerModel refinement = layer.getContainerAtPosition(position);
				psmContainer.addRefinement(refinement);
			}
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM Transformer element - outputcontainer attribute 
	 * @param transformer
	 * @param node
	 */
	private void addPsmTransformerOutputContainers(ActionTransformerModel transformer, Node node) {
		Node psmParamData = node.getAttributes().getNamedItem("outputcontainer");
		if(psmParamData == null)
			return;
		String psmParamDataValues = psmParamData.getNodeValue();
		String[] params = psmParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@psms/@psmcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addOutputParam(pData);
		}
	}

	/**
	 * See doc/metamodel.png: PSM Transformer element - inputcontainer attribute 
	 * @param transformer
	 * @param node
	 */
	private void addPsmTransformerInputContainers(ActionTransformerModel transformer, Node node) {
		Node psmParamData = node.getAttributes().getNamedItem("inputcontainer");
		if(psmParamData == null)
			return;
		String psmParamDataValues = psmParamData.getNodeValue();
		String[] params = psmParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@psms/@psmcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addInputParam(pData);
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM Transformer element - crossPsmRefmnt attribute 
	 * @param psmTransformer
	 * @param node
	 */
	private void addPsmTransformerCrossRefinement(ActionTransformerModel psmTransformer, Node node){
		Node psmTransformerRefinement = node.getAttributes().getNamedItem("crossPsmRefmnt");
		if(psmTransformerRefinement == null)
			return;
		String implementedAsData = psmTransformerRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			if(element.contains("//@psms/@psmtransformers.")){
				element = element.replace("//@psms/@psmtransformers.", "");
				int position = Integer.parseInt(element);
				ActionTransformerModel refinement = layer.getTransformerAtPosition(position);
				psmTransformer.addRefinement(refinement);
			} else if(element.contains("//@isms/@ismtransformers.")){
				element = element.replace("//@isms/@ismtransformers.", "");
				int position = Integer.parseInt(element);
				ActionTransformerModel refinement = layer.getRefinementLayer().getTransformerAtPosition(position);
				psmTransformer.addRefinement(refinement);
			}
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM Transformer element - psmRefmnt attribute 
	 * @param psmTransformer
	 * @param node
	 */
	private void addPsmTransformerInnerRefinement(ActionTransformerModel psmTransformer, Node node){
		Node psmTransformerRefinement = node.getAttributes().getNamedItem("psmRefmnt");
		if(psmTransformerRefinement == null)
			return;
		String implementedAsData = psmTransformerRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@psms/@psmtransformers.", "");
			int position = Integer.parseInt(element);
			ActionTransformerModel refinement = layer.getTransformerAtPosition(position);
			psmTransformer.addRefinement(refinement);
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM system element - sysimplementedas attribute
	 * @param system
	 * @param node
	 */
	private void addPsmSystemCrossRefinement(SystemModel system, Node node) {
		Node psmSystemRefinement = node.getAttributes().getNamedItem("sysimplementedas");
		if(psmSystemRefinement == null)
			return;
		String implementedAsData = psmSystemRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			if(element.contains("//@isms/@ismsystems.")){
				element = element.replace("//@isms/@ismsystems.", "");
				int position = Integer.parseInt(element);
				SystemModel refinement = layer.getRefinementLayer().getSystemAtPosition(position);
				system.addRefinement(refinement);
			}
			else if(element.contains("//@psms/@psmsystems.")){
				element = element.replace("//@psms/@psmsystems.", ""); //for the case when we have inner refinement
				int position = Integer.parseInt(element);
				SystemModel refinement = layer.getSystemAtPosition(position);
				system.addRefinement(refinement);
			}
		}
	}
	
	/**
	 * See doc/metamodel.png: PSM system element - systemtransformers attribute
	 * @param system
	 * @param node
	 */
	private void addPsmSystemTransformers(SystemModel system, Node node) {
		Node psmTransformers = node.getAttributes().getNamedItem("systemtransformers");
		if(psmTransformers==null){
			return;
		}
		String systemsData = psmTransformers.getNodeValue();
		String[] systems = systemsData.split(" ");
		for(int i =0; i<systems.length; i++){
			String element = systems[i].trim();
			element = element.replace("//@psms/@psmtransformers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for system: "+system.toStringShort(), ex);}
			ActionTransformerModel transformer = layer.getTransformerAtPosition(position);
			system.addOperation(transformer);
			transformer.setParentSystem(system);
		}
	}
	
	
	/**
	 * See doc/metamodel.png: PSM system - systemassociation attribute
	 * @param sys
	 * @param node
	 */
	private void addPsmSystemInnerAssociation(SystemModel sys, Node node) {
		Node sysAssociations = node.getAttributes().getNamedItem("systemassociation");
		if(sysAssociations==null){
			return;
		}
		String systemsData = sysAssociations.getNodeValue();
		String[] systems = systemsData.split(" ");
		for(int i =0; i<systems.length; i++){
			String element = systems[i].trim();
			element = element.replace("//@psms/@psmsystems.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for system: "+sys.toStringShort(), ex);}
			SystemModel system = layer.getSystemAtPosition(position);
			sys.addAssociationLink(system);
		}
		
	}
	
	/**************************************************************
	 * ISM level
	 * every element has the attributes defined in the doc/metamodel.png
	 * */
			

	/**
	 * See doc/metamodel.png: ISM element implecontaiener - attribute implecontainerassociation
	 * @param ismContainer
	 * @param node
	 */
	private void addIsmContainerInnerRefinement(DataContainerModel ismContainer, Node node){
		Node ismAssociation = node.getAttributes().getNamedItem("implecontainerassociation");
		if(ismAssociation==null){
			return;
		}
		String associationData = ismAssociation.getNodeValue();
		String[] associatons = associationData.split(" ");
		for(int i =0; i<associatons.length; i++){
			String element = associatons[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for ismContainer: "+ismContainer.toString(), ex);}
			DataContainerModel refinement = layer.getContainerAtPosition(position);
			ismContainer.addAssociation(refinement);
		}
	}

	/**
	 * See metamodel: element implecontainer - no cross association
	 * @param container
	 * @param node
	 */
	private void addIsmContainerCrossRefinement(DataContainerModel container, Node node) {
		// ISM container - no cross layer refinement in the model
	}
	
	/**
	 * See doc/metamodel.png: ISM element transformer - attribute ismRefmnt
	 * @param ismTransformer
	 * @param node
	 */
	private void addIsmTransformerInnerRefinement(ActionTransformerModel ismTransformer, Node node){
		Node ismTransformerRefinement = node.getAttributes().getNamedItem("ismRefmnt");
		if(ismTransformerRefinement == null)
			return;
		String implementedAsData = ismTransformerRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@isms/@ismtransformers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for transformer: "+ismTransformer.toStringShort(), ex);}
			ActionTransformerModel refinement = layer.getTransformerAtPosition(position);
			ismTransformer.addRefinement(refinement);
		}
	}
	
	/**
	 * There is no cross refmnt in the model for ISM
	 * see ./doc/metamodel.png
	 * if needed can be added with addIsmTransformerInnerRefinement with the attribute ismRefmnt
	 * @param ismTransformer
	 * @param node
	 */
	private void addIsmTransformerCrossRefinement(ActionTransformerModel ismTransformer, Node node){
		// There is no cross refmnt in the model for ISM
		// see ./doc/metamodel.png
		// if needed can be added with addIsmTransformerInnerRefinement with the attribute ismRefmnt
	}
	
	/**
	 * See doc/metamodel.png: ISM element transformer - attribute outputimplecontainer
	 * @param transformer
	 * @param node
	 */
	private void addIsmTransformerOutputContainers(ActionTransformerModel transformer, Node node) {
		Node ismParamData = node.getAttributes().getNamedItem("outputimplecontainer");
		if(ismParamData == null)
			return;
		String ismParamDataValues = ismParamData.getNodeValue();
		String[] params = ismParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for transformer: "+transformer.toStringShort(), ex);}
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addOutputParam(pData);
		}
	}

	/**
	 * See doc/metamodel.png: ISM element transformer - attribute inputimplecontainer
	 * @param transformer
	 * @param node
	 */
	private void addIsmTransformerInputContainers(ActionTransformerModel transformer, Node node) {
		Node ismParamData = node.getAttributes().getNamedItem("inputimplecontainer");
		if(ismParamData == null)
			return;
		String ismParamDataValues = ismParamData.getNodeValue();
		String[] params = ismParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for transformer: "+transformer.toStringShort(), ex);}
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addInputParam(pData);
		}
	}
	
	
	/**
	 * See doc/metamodel.png: ISM element implesystem - attribute implesystemtransformers
	 * @param system
	 * @param node
	 */
	private void addIsmSystemTransformers(SystemModel system, Node node) {
		Node ismTransformers = node.getAttributes().getNamedItem("implesystemtransformers");
		if(ismTransformers==null){
			return;
		}
		String systemsData = ismTransformers.getNodeValue();
		String[] systems = systemsData.split(" ");
		for(int i =0; i<systems.length; i++){
			String element = systems[i].trim();
			element = element.replace("//@isms/@ismtransformers.", "");
			int position = 0 ;
			try{
				position = Integer.parseInt(element);
			} catch(Exception ex){logger.error("Invalid definition for system: "+system.toStringShort(), ex);}
			ActionTransformerModel transformer = layer.getTransformerAtPosition(position);
			system.addOperation(transformer);
			transformer.setParentSystem(system);
		}
	}
	
	/**
	 * See doc/metamodel.png: ISM element implesystem - implesystemassociation attribute
	 * @param sys
	 * @param node
	 */
	private void addIsmSystemInnerAssociation(SystemModel sys, Node node) {
		Node sysAssociations = node.getAttributes().getNamedItem("implesystemassociation");
		if(sysAssociations==null){
			return;
		}
		String systemsData = sysAssociations.getNodeValue();
		String[] systems = systemsData.split(" ");
		for(int i =0; i<systems.length; i++){
			String element = systems[i].trim();
			element = element.replace("//@isms/@ismsystems.", "");
			int position = Integer.parseInt(element);
			SystemModel system = layer.getSystemAtPosition(position);
			sys.addAssociationLink(system);
		}
	}
	
	/**
	 * See doc/metamodel.png: ISM element implesystem
	 * There is no cross refmnt in the model for ISM system
	 * @param system
	 * @param node
	 */
	private void addIsmSystemCrossRefinement(SystemModel system, Node node) {
		// There is no cross refmnt in the model for ISM
	}
	
}
