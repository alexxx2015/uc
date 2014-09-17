package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	private ArrayList<ActionTransformerModel> associations;
	
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
		this.refinements = new ArrayList<>();
		this.inputParams = new ArrayList<>();
		this.outputParams = new ArrayList<>();
		this.associations = new ArrayList<>();
		this.synonyms = new ArrayList<String>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
		this.isMerged = false;
		this.parentSystem = null;
		this.refinementType = RefinementType.SET;
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
	
	public void addAssociationLink(ActionTransformerModel act){
		if(act==null)
			return;
		this.associations.add(act);
	}
	
	public ArrayList<ActionTransformerModel> getAssociationLinks(){
		return this.associations;
	}
	
	public ActionTransformerModel getAssociationByName(String name){
		for(ActionTransformerModel a : this.associations){
			if(a.name.equals(name))
				return a;
			if(a.alsoKnownAs(name))
				return a;
		}
		return null;
	}
	
	public ActionTransformerModel getAssociationLink(ActionTransformerModel assoc){
		for(ActionTransformerModel a : this.associations){
			if(a.equals(assoc))
				return a;
		}
		return null;
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
	
	/**
	 * Note: No need for synonyms because the refinement is either at PSM or ISM level.
	 * There is no inner refinement for actions defined at PIM.
	 * @param name
	 * @return
	 */
	public ActionTransformerModel getRefinementByName(String name){
		if(name==null)
			return null;		
		for(ActionTransformerModel ref : this.refinements){
			if(ref.name.equals(name))
				return ref;
		}
		return null;
	}
	
	public ActionTransformerModel getRefinement(ActionTransformerModel a){
		if(a==null)
			return null;		
		for(ActionTransformerModel ref : this.refinements){
			if(ref.equals(a))
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
		String systemName = this.parentSystem == null ? null : this.parentSystem.getName();
		result += this.indentationLevel+name +" - "+ layerType.name()+" ref: "+ this.refinementType.name() +" sys: "+ systemName;
		String refinedAs = "";
		for(ActionTransformerModel ref : this.refinements){
			refinedAs += " "+ ref.name +"-"+ ref.layerType.name(); 
		}
		result += refinedAs;
		return result;
	}

	public String toStringShort(){
		String result ="";
		String systemName = this.parentSystem == null ? null : this.parentSystem.getName();
		result += name +" {"+ layerType.name()+" "+ this.refinementType.name() +" sys:"+ systemName +"}";
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
	
	private void addPimActionAttributes(Element action){
		//process synonyms
		String synonymName = "synonym";
		String synonymValue ="";
		for(String syn : this.synonyms){
			synonymValue += syn + " ";
		}
		
		String associationAttribute = "actionassociation";
		String associationData = "";
		boolean existsAssociation = false;
		for(ActionTransformerModel assoc : this.associations){
			associationData += "//@pims/@pimaction." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
	
		//process refinements
		String refinementAttribute = "actionRefmnt";
		String refinementData = "";
		boolean existsRefinement = false;
		for(ActionTransformerModel ref : this.refinements){
			String refLevel = "//@psms/@psmtransformers.";
			refinementData += refLevel + ref.xmlPosition +" ";
			existsRefinement = true;
		}

		//process parameters
		String paramAttribute = "paramData";
		String paramData = "";
		boolean existsParam = false;
		for(DataContainerModel p : this.inputParams){
			String refLevel = "//@pims/@pimdata.";
			refinementData += refLevel + p.getXmlPosition() +" ";
			existsParam = true;
		}
		
		action.setAttribute(synonymName, synonymValue);
		if(existsParam)
			action.setAttribute(paramAttribute, paramData);
		if(existsRefinement)
			action.setAttribute(refinementAttribute, refinementData);
		if(existsAssociation)
			action.setAttribute(associationAttribute, associationData);
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * Checks the name, synonym and system.
	 */
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
		else 
			if(this.parentSystem.equals(obj.parentSystem))
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

	@Override
	public int hashCode() {
		String unique = name + "#"+ layerType.name()+"#"+ (this.parentSystem==null ? "" : this.parentSystem.getName());
		return unique.hashCode();
	}
	
	public void markAsMerged(){
		this.isMerged = true;
	}
	
	public boolean isMerged() {
		return isMerged;
	}
	
	/**
	 * Empty the list of refinement elements.
	 */
	public void resetRefinement(){
		this.refinements.clear();
	}


	/**
	 * The Action/Transformer element contains as input param 
	 * the container element sent as parameter to the method.
	 * @param dc
	 * @return
	 */
	public boolean hasInputParam(DataContainerModel dc) {
		for(DataContainerModel in : this.inputParams){
			if(in.equals(dc))
				return true;
		}
		return false;
	}

	/**
	 * The Transformer element contains as output param 
	 * the container element sent as parameter to the method.
	 * @param dc
	 * @return
	 */
	public boolean hasOutputParam(DataContainerModel dc) {
		for(DataContainerModel in : this.outputParams){
			if(in.equals(dc))
				return true;
		}
		return false;
	}
}
