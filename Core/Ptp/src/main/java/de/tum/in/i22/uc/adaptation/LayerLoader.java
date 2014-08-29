package de.tum.in.i22.uc.adaptation;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel;
import de.tum.in.i22.uc.adaptation.model.DataContainerModel;
import de.tum.in.i22.uc.adaptation.model.ActionTransformerModel.RefinementType;
import de.tum.in.i22.uc.adaptation.model.DomainModel.DomainLayerType;
import de.tum.in.i22.uc.adaptation.model.LayerModel;
import de.tum.in.i22.uc.adaptation.model.SystemModel;

/**
 * @author Cipri
 *
 */
public class LayerLoader {
	
	private LayerModel layer ;
	
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
		int containersIndex = 0;
		/*use this map for the inner refinement*/
		HashMap<DataContainerModel, Node> containerNodesMap = new HashMap<DataContainerModel,Node>();
		for(int i=0; i<containersCounter; i++){
			Node node = containers.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			DataContainerModel container = new DataContainerModel(name, layer.getType());
			container.setParenLayer(layer);
			containerNodesMap.put(container, node);
			container.setXmlPosition(containersIndex);
			containersIndex ++;
			
			switch(layer.getType()){
				case PIM:
					addPimContainerCrossRefinement(container, node);
					break;
				case PSM:
					addPsmContainerCrossRefinement(container, node);
					break;
				case ISM: 					
					addIsmContainerCrossRefinement(container, node);					
				default:
					break;
			}
			
			layer.addDataContainer(container);
		}
		
		/* do the inner refinement */
		for(DataContainerModel dc : containerNodesMap.keySet()){
			Node node = containerNodesMap.get(dc);

			switch(layer.getType()){
			case PIM:
				addPimContainerInnerRefinement(dc, node);
				break;
			case PSM:
				addPsmContainerInnerRefinement(dc, node);
				break;
			case ISM:
				addIsmContainerInnerRefinement(dc, node);
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
		int transformersIndex = 0;
		/*use this map for the inner refinement*/
		HashMap<ActionTransformerModel, Node> transformersNodesMap = new HashMap<ActionTransformerModel,Node>();
		for(int i=0; i<transformersCounter; i++){
			Node node = transformers.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			
			RefinementType refType = RefinementType.SET;
			try{
				String refTypeAttr = "";
				refTypeAttr = node.getAttributes().getNamedItem("refType").getNodeValue();
				if(refTypeAttr.equals("seqRefmnt"))
					refType = RefinementType.SEQ;
			}catch(Exception ex){}
			
			ActionTransformerModel transformer = new ActionTransformerModel(name, layer.getType());
			transformer.setParenLayer(layer);
			transformer.setRefinementType(refType);
			transformersNodesMap.put(transformer, node);
			transformer.setXmlPosition(transformersIndex);
			transformersIndex ++;
			
			switch(layer.getType()){
				case PIM:
					addPimActionParamData(transformer, node);
					addPimActionCrossRefinement(transformer, node);
					break;
				case PSM:
					addPsmTransformerInputContainers(transformer, node);
					addPsmTransformerOutputContainers(transformer, node);
					addPsmTransformerCrossRefinement(transformer, node);
					break;
				case ISM:
					addIsmTransformerInputContainers(transformer, node);
					addIsmTransformerOutputContainers(transformer, node);
					addIsmTransformerCrossRefinement(transformer, node);
				default:
					break;
			}
			layer.addActionTransformer(transformer);
		}
		
		/* do the inner refinement */
		for(ActionTransformerModel at : transformersNodesMap.keySet()){
			Node node = transformersNodesMap.get(at);

			switch(layer.getType()){
			case PIM:
				addPimActionInnerRefinement(at, node);
				break;
			case PSM:
				addPsmTransformerInnerRefinement(at, node);
				break;
			case ISM:
				addIsmTransformerInnerRefinement(at, node);
			default:
				break;
			}
		}
		
	}
	
	public void addSystems(NodeList systems) {
		if(systems == null)
			return;
		int systemsCounter = systems.getLength();
		int systemsIndex = 0;
		for(int i=0; i<systemsCounter; i++){
			Node node = systems.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			SystemModel system = new SystemModel(name, layer.getType());
			system.setParenLayer(layer);
			system.setXmlPosition(systemsIndex);
			systemsIndex ++;
			
			switch(layer.getType()){
				case PIM:
					break;
				case PSM:
					addPsmSystemTransformers(system,node);
					addPsmSystemCrossRefinement(system,node);
					break;
				case ISM: 
					addIsmSystemTransformers(system,node);
					addIsmSystemCrossRefinement(system,node);
				default:
					break;
			}
			
			layer.addSystem(system);
		}
	}
	

	/**************************************************************
	 * PIM level operations
	 * */
	
	private void addPimActionParamData(ActionTransformerModel transformer, Node node) {
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
			transformer.addInputParam(pData);
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
			element = element.replace("//@psms/@psmtransformers.", "");
			int position = Integer.parseInt(element);
			ActionTransformerModel refinement = layer.getRefinementLayer().getTransformerAtPosition(position);
			pimAction.addRefinement(refinement);
		}
	}
	
	private void addPimContainerCrossRefinement(DataContainerModel pimContainer, Node node){
		Node pimStoredIn = node.getAttributes().getNamedItem("storedin");
		if(pimStoredIn == null)
			return;
		String implementedAsData = pimStoredIn.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@psms/@psmcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel refinement = layer.getRefinementLayer().getContainerAtPosition(position);
			pimContainer.addRefinement(refinement);
		}
	}
	
	private void addPimActionInnerRefinement(ActionTransformerModel pimAction, Node node){
		Node pimActionAssociation = node.getAttributes().getNamedItem("actionassociation");
		if(pimActionAssociation == null)
			return;
		//TODO: I don't know how the association for the actions looks like. Is it a child node or an attribute?
		
	}
	
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
	 * PSM level operations
	 * */
	
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
	
	private void addPsmSystemCrossRefinement(SystemModel system, Node node) {
		Node psmSystemRefinement = node.getAttributes().getNamedItem("sysimplementedas");
		if(psmSystemRefinement == null)
			return;
		String implementedAsData = psmSystemRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@isms/@ismsystems.", "");
			int position = Integer.parseInt(element);
			SystemModel refinement = layer.getRefinementLayer().getSystemAtPosition(position);
			system.addRefinement(refinement);
		}
	}
	
	private void addPsmTransformerCrossRefinement(ActionTransformerModel psmTransformer, Node node){
		Node psmTransformerRefinement = node.getAttributes().getNamedItem("crossPsmRefmnt");
		if(psmTransformerRefinement == null)
			return;
		String implementedAsData = psmTransformerRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@isms/@ismtransformers.", "");
			int position = Integer.parseInt(element);
			ActionTransformerModel refinement = layer.getRefinementLayer().getTransformerAtPosition(position);
			psmTransformer.addRefinement(refinement);
		}
	}
	
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
	
	private void addPsmContainerCrossRefinement(DataContainerModel psmContainer, Node node){
		//applies at PSM
		Node psmImplementedAs = node.getAttributes().getNamedItem("contimplementedas");
		if(psmImplementedAs == null)
			return;
		String implementedAsData = psmImplementedAs.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel refinement = layer.getRefinementLayer().getContainerAtPosition(position);
			psmContainer.addRefinement(refinement);
		}
	}
	
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
			int position = Integer.parseInt(element);
			ActionTransformerModel transformer = layer.getTransformerAtPosition(position);
			system.addOperation(transformer);
			transformer.setParentSystem(system);
		}
	}
	
	/**************************************************************
	 * ISM level operations
	 * */
	
	private void addIsmSystemCrossRefinement(SystemModel system, Node node) {
		// There is no cross refmnt in the model for ISM
	}
	
	private void addIsmTransformerCrossRefinement(ActionTransformerModel ismTransformer, Node node){
		// There is no cross refmnt in the model for ISM
	}
	
	private void addIsmTransformerInnerRefinement(ActionTransformerModel ismTransformer, Node node){
		Node ismTransformerRefinement = node.getAttributes().getNamedItem("ismRefmnt");
		if(ismTransformerRefinement == null)
			return;
		String implementedAsData = ismTransformerRefinement.getNodeValue();
		String[] refinements = implementedAsData.split(" ");
		for(int i =0; i<refinements.length; i++){
			String element = refinements[i].trim();
			element = element.replace("//@psms/@psmtransformers.", "");
			int position = Integer.parseInt(element);
			ActionTransformerModel refinement = layer.getTransformerAtPosition(position);
			ismTransformer.addRefinement(refinement);
		}
	}
	
	private void addIsmTransformerOutputContainers(ActionTransformerModel transformer, Node node) {
		Node ismParamData = node.getAttributes().getNamedItem("outputimplecontainer");
		if(ismParamData == null)
			return;
		String ismParamDataValues = ismParamData.getNodeValue();
		String[] params = ismParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addOutputParam(pData);
		}
	}

	private void addIsmTransformerInputContainers(ActionTransformerModel transformer, Node node) {
		Node ismParamData = node.getAttributes().getNamedItem("inputimplecontainer");
		if(ismParamData == null)
			return;
		String ismParamDataValues = ismParamData.getNodeValue();
		String[] params = ismParamDataValues.split(" ");
		for(int i =0; i<params.length; i++){
			String element = params[i].trim();
			element = element.replace("//@isms/@ismcontainers.", "");
			int position = Integer.parseInt(element);
			DataContainerModel pData = layer.getContainerAtPosition(position);
			transformer.addInputParam(pData);
		}
	}
	
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
			int position = Integer.parseInt(element);
			DataContainerModel refinement = layer.getContainerAtPosition(position);
			ismContainer.addAssociation(refinement);
		}
	}


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
			int position = Integer.parseInt(element);
			ActionTransformerModel transformer = layer.getTransformerAtPosition(position);
			system.addOperation(transformer);
			transformer.setParentSystem(system);
		}
	}
	
	private void addIsmContainerCrossRefinement(DataContainerModel container, Node node) {
		// ISM container - no cross layer refinement in the model
	}
	
}
