package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.adaptation.model.DataContainerModel.AssociationType;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

/**
 * @author Cipri
 *
 */
public class ActionTransformerModel {

	/**
	 * An Action or a Transformer can be further refined 
	 * as a SET or as a SEQUENCE of other events. 
	 */
	public static enum RefinementType{
		SET,
		SEQ
	}
	
	private String name;
	/**
	 * After merging, there can be the same action with multiple similar names.
	 * To avoid duplicating the entry, a synonym list is kept.
	 */
	private ArrayList<String> synonyms;
	
	/**
	 * PIM, PSM, ISM
	 */
	private LayerType layerType;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
	/**
	 * Used for pretty printing on console.
	 */
	private String indentationLevel ;
	
	private RefinementType refinementType;
	
	private ArrayList<ActionTransformerModel> refinements;
	
	/**
	 * PIM - equivalent of paramData
	 */
	private ArrayList<DataContainerModel> inputParams;
	
	private ArrayList<DataContainerModel> outputParams;
	
	private LayerModel parentLayer;
	
	private SystemModel parentSystem;
	
	private boolean isMerged ;
	
	public ActionTransformerModel(String name, LayerType type){
		this.name = name;
		this.layerType = type;
		refinements = new ArrayList<>();
		inputParams = new ArrayList<>();
		outputParams = new ArrayList<>();
		synonyms = new ArrayList<String>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
		this.isMerged = false;
		this.parentSystem = null;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public LayerType getLayerType(){
		return this.layerType;
	}
	
	/**
	 * The Action/Transformer is further refined as a SET/SEQ of events.
	 * @param type
	 */
	public void setRefinementType(RefinementType type){
		this.refinementType = type;
	}
	
	public RefinementType getRefinementType(){
		return this.refinementType;
	}
	
	public void addSynonym(String name){
		if(name==null)
			return;
		if(this.name.equals(name))
			return;
		if(!this.synonyms.contains(name))
			this.synonyms.add(name);
	}
	
	public boolean alsoKnownAs(String synonym){
		if(synonym == null)
			return false;
		return this.synonyms.contains(synonym);
	}
	
	public ArrayList<String> getSynonyms(){
		return this.synonyms;
	}
	
	public void setXmlPosition(int position){
		this.xmlPosition = position;
	}
	
	public int getXmlPosition(){
		return  this.xmlPosition;
	}
	
	public void setParenLayer(LayerModel layer){
		this.parentLayer = layer;
		this.indentationLevel = layer.indentation +"	";
	}
	
	public void setParentSystem(SystemModel system){
		this.parentSystem = system;
	}
	
	public SystemModel getParentSystem(){
		return this.parentSystem;
	}
	
	public ArrayList<ActionTransformerModel> getRefinements(){
		return this.refinements;
	}
	
	public void addRefinement(ActionTransformerModel refinedAs){
		if(refinedAs == null)
			return;
		this.refinements.add(refinedAs);
	}
	
	public ActionTransformerModel getRefinementByName(String name){
		if(name==null)
			return null;
		//no need for synonyms because the refinement is either at PSM or ISM level
		for(ActionTransformerModel ref : this.refinements){
			if(ref.name.equals(name))
				return ref;
		}
		return null;
	}
	
	/**
	 * PIM - paramData
	 * PSM, ISM - input container
	 * @param input
	 */
	public void addInputParam(DataContainerModel input){
		if(input == null)
			return;
		this.inputParams.add(input);
	}
	
	public ArrayList<DataContainerModel> getInputParams(){
		return this.inputParams;
	}
	
	/**
	 * PIM - not allowed; acts as NOP operation
	 * PSM, 
	 * @param output
	 */
	public void addOutputParam(DataContainerModel output){
		if(output == null)
			return;
		if(this.parentLayer.equals(DomainModel.LayerType.PIM))
			return;
		this.outputParams.add(output);
	}
	
	public ArrayList<DataContainerModel> getOutputParams(){
		return this.outputParams;
	}
	
	public String toString(){
		String result ="";
		result += this.indentationLevel+name +" - "+ layerType.name()+" ref: "+ this.refinementType.name();
		String refinedAs = "";
		for(ActionTransformerModel ref : this.refinements){
			refinedAs += " "+ ref.name +"-"+ ref.layerType.name(); 
		}
		result += refinedAs;
		return result;
	}

	public Element getXmlNode(Document doc){
		if(doc == null)
			return null;
		Element element = null;
		String layerType = "";
		switch (this.layerType) {
		case PIM:			
			layerType = "pimaction";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addPimActionAttributes(element);
			break;
		case PSM:
			layerType = "psmtransformer";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addPsmTransformerAttributes(element);
			break;
		case ISM:
			layerType = "ismtransformer";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addIsmTransformerAttributes(element);
			break;
		default:
			break;
		}
		
		return element;
	}
	
	private void addPimActionAttributes(Element data){
//		//process synonyms
//		String synonymName = "synonym";
//		String synonymValue ="";
//		for(String syn : this.synonyms){
//			synonymValue += syn + " ";
//		}
//		data.setAttribute(synonymName, synonymValue);
//		
//		String associationNodeName = "dataAssoLinks";
//		String associationTypeName = "assoType";
//		
//		//process aggregations
//		Element aggregation = data.getOwnerDocument().createElement(associationNodeName);
//		String associationType = "isAggregationOf";
//		aggregation.setAttribute(associationTypeName, associationType);
//		String associationDataName = "targetAssoData";
//		String associationData = "";
//		boolean existsAssociation = false;
//		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
//			associationData += "//@pims/@pimdata." + assoc.xmlPosition +" ";
//			existsAssociation = true;
//		}
//		if(existsAssociation){
//			aggregation.setAttribute(associationDataName, associationData);
//			data.appendChild(aggregation);
//		}
//		
//		// process compositions
//		Element composition = data.getOwnerDocument().createElement(associationNodeName);
//		associationType = "isCompositionOf";
//		composition.setAttribute(associationTypeName, associationType);
//		String compostionData = "";
//		boolean existsComposition = false;
//		for(DataContainerModel assoc : this.associations.get(AssociationType.COMPOSITION)){
//			compostionData += "//@pims/@pimdata." + assoc.xmlPosition +" ";
//			existsComposition = true;
//		}
//		if(existsComposition){
//			composition.setAttribute(associationDataName, compostionData);
//			data.appendChild(composition);
//		}
//		
//		//process refinements
//		String refinementAttribute = "storedin";
//		String refinementData = "";
//		boolean existsRefinement = false;
//		for(DataContainerModel ref : this.refinements){
//			String refLevel = "";
//			if(ref.getLayerType().equals(LayerType.PIM))
//				refLevel = "//@pims/@pimdata.";
//			else if(ref.getLayerType().equals(LayerType.PSM))
//				refLevel = "//@psms/@psmcontainers.";
//			refinementData += refLevel + ref.xmlPosition +" ";
//			existsRefinement = true;
//		}
//		if(existsRefinement)
//			data.setAttribute(refinementAttribute, refinementData);
	}
	
	private void addPsmTransformerAttributes(Element container){
//		String associationAttribute = "containersassociation";
//		String associationData = "";
//		boolean existsAssociation = false;
//		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
//			associationData += "//@psms/@psmcontainers." + assoc.xmlPosition +" ";
//			existsAssociation = true;
//		}
//		if(existsAssociation)
//			container.setAttribute(associationAttribute, associationData);
//		
//		String refinementAttribute = "contimplementedas";
//		String refinementData = "";
//		boolean existsRefinement = false;
//		for(DataContainerModel ref : this.refinements){
//			String refLevel = "";
//			if(ref.getLayerType().equals(LayerType.PSM))
//				refLevel = "//@psms/@psmcontainers.";
//			else if(ref.getLayerType().equals(LayerType.ISM))
//				refLevel = "//@isms/@ismcontainers.";
//			refinementData += refLevel + ref.xmlPosition +" ";
//			existsRefinement = true;
//		}
//		if(existsRefinement)
//			container.setAttribute(refinementAttribute, refinementData);
	}
	
	private void addIsmTransformerAttributes(Element container){
		//TODO: be careful to the sequences and the position of the elements
//		String associationAttribute = "implecontainerassociation";
//		String associationData = "";
//		boolean existsAssociation = false;
//		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
//			associationData += "//@isms/@ismcontainers." + assoc.xmlPosition +" ";
//			existsAssociation = true;
//		}
//		if(existsAssociation)
//			container.setAttribute(associationAttribute, associationData);
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if(! (o instanceof ActionTransformerModel))
			return false;
		ActionTransformerModel obj = (ActionTransformerModel) o;
		if(!this.layerType.equals(obj.layerType))
			return false;
		if(parentSystem==null&&obj.parentSystem!=null)
			return false;
		if(parentSystem!=null&&obj.parentSystem==null)
			return false;
		
		boolean systemOk = false;
		if(parentSystem==null && obj.parentSystem==null)
			systemOk = true;
		else if(!this.parentSystem.equals(obj.parentSystem))
			systemOk = true;
		if(!systemOk)
			return false;
		
		if(this.name.equals(obj.name))
			return true;
		if(this.alsoKnownAs(obj.name))
			return true;
		if(obj.alsoKnownAs(name))
			return true;
		
		return false;
	}

	public void markAsMerged(){
		this.isMerged = true;
	}
	
	public boolean isMerged() {
		return isMerged;
	}
}
