package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

public class DataContainerModel {

	public static enum AssociationType{
		COMPOSITION,
		AGGREGATION
	}
	
	private String name;
	/**
	 * After merging, there can be the same action with multiple similar names.
	 * To avoid duplicating the entry, an alias list is kept.
	 */
	private ArrayList<String> synonyms;
	
	private LayerType layerType ;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
	/**
	 * Used for pretty printing on console.
	 */
	private String indentationLevel ;
	
	private ArrayList<DataContainerModel> refinements;
	
	/**
	 * Associations are also known as inner-links in the paper.
	 */
	private Map<AssociationType, ArrayList<DataContainerModel>> associations;
	
	private LayerModel parentLayer;
	
	private boolean isMerged ;
	
	public DataContainerModel(String name, LayerType type){
		this.name = name;
		this.layerType = type;
		this.xmlPosition = -1;
		this.isMerged = false;
		this.refinements = new ArrayList<>();
		synonyms = new ArrayList<String>();
		ArrayList<DataContainerModel> compositions = new ArrayList<DataContainerModel>();
		ArrayList<DataContainerModel> aggregations = new ArrayList<DataContainerModel>();
		this.associations = new HashMap<>();
		this.associations.put(AssociationType.AGGREGATION, aggregations);
		this.associations.put(AssociationType.COMPOSITION, compositions);
		this.indentationLevel = "";
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String newName){
		if(newName == null)
			return;
		this.name = newName;
	}
	
	public LayerType getLayerType(){
		return this.layerType;
	}
	
	public ArrayList<DataContainerModel> getRefinements(){
		return this.refinements;
	}
	
	public DataContainerModel getRefinementByName(String name){
		if(name==null)
			return null;
		//no need for synonyms because the refinement is either at PSM or ISM level
		for(DataContainerModel ref : this.refinements){
			if(ref.name.equals(name))
				return ref;
		}
		return null;
	}
	
	/**
	 * For PSM, ISM there is only aggregation type as the default association.
	 * @return
	 */
	public ArrayList<DataContainerModel> getAssociations(){
		ArrayList<DataContainerModel> aggreg = this.associations.get(AssociationType.AGGREGATION);
		return aggreg;
	}
	
	public ArrayList<DataContainerModel> getAggregations(){
		ArrayList<DataContainerModel> aggreg = this.associations.get(AssociationType.AGGREGATION);
		return aggreg;
	}
	
	public ArrayList<DataContainerModel> getCompositions(){
		ArrayList<DataContainerModel> comp = this.associations.get(AssociationType.COMPOSITION);
		return comp;
	}
	
	public DataContainerModel getAssociationLink(String name){
		if(name == null)
			return null;
		ArrayList<DataContainerModel> assoc = this.associations.get(AssociationType.AGGREGATION);
		for(DataContainerModel link : assoc){
			if(link.name.equals(name))
				return link;
		}
		return null;
	}
	
	public DataContainerModel getAggregationLink(String name){
		if(name == null)
			return null;
		ArrayList<DataContainerModel> aggreg = this.associations.get(AssociationType.AGGREGATION);
		for(DataContainerModel link : aggreg){
			if(link.name.equals(name))
				return link;
		}
		return null;
	}
	
	public DataContainerModel getCompositionLink(String name){
		if(name == null)
			return null;
		ArrayList<DataContainerModel> comp = this.associations.get(AssociationType.COMPOSITION);
		for(DataContainerModel link : comp){
			if(link.name.equals(name))
				return link;
		}
		return null;
	}
	
	public void markAsMerged(){
		this.isMerged = true;
	}
	
	public boolean isMerged(){
		return this.isMerged;
	}
	
	/**
	 * Adds a synonym of the name of the element.
	 * @param synonym
	 */
	public void addSynonym(String synonym){
		if(synonym == null)
			return;
		if(this.name.equals(synonym))
			return;
		if(!this.synonyms.contains(synonym))
			this.synonyms.add(synonym);
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
	
	public LayerModel getLayerModel(){
		return this.parentLayer;
	}
	
	public void addRefinement(DataContainerModel refinedAs){
		if(refinedAs == null)
			return;
		this.refinements.add(refinedAs);
	}
	
	/**
	 * Association is used at PSM and ISM layer.
	 * It is not specified if it aggregation or composition.
	 * By default, it is considered aggregation.
	 * In the paper this is also named an innerLink.
	 * @param association
	 */
	public void addAssociation(DataContainerModel association){
		if(association == null)
			return;
		ArrayList<DataContainerModel> aggreagations = this.associations.get(AssociationType.AGGREGATION);
		aggreagations.add(association);
	}
	
	public void addComposition(DataContainerModel composition){
		if(composition == null)
			return;
		ArrayList<DataContainerModel> compositions = this.associations.get(AssociationType.COMPOSITION);
		compositions.add(composition);
	}
	
	public void addAggregation(DataContainerModel aggregation){
		if(aggregation == null)
			return;
		ArrayList<DataContainerModel> aggreagations = this.associations.get(AssociationType.AGGREGATION);
		aggreagations.add(aggregation);
	}
	
	public String toString(){
		String result ="";
		result += this.indentationLevel+ name + " "+this.synonyms +" - "+ layerType.name();
		
		String aggregations = "\n	"+this.indentationLevel +"aggregations:";
		boolean existsAggregation = false;
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			aggregations += " "+ this.indentationLevel +assoc.toString();
			existsAggregation = true;
		}
		String compositions =  "\n	"+this.indentationLevel +"compositions:";
		boolean existsComposition = false;
		for(DataContainerModel comp : this.associations.get(AssociationType.COMPOSITION)){
			compositions += " "+ this.indentationLevel +comp.toString();
			existsComposition = true;
		}
		if(existsAggregation)
			result += aggregations;
		if(existsComposition)
			result += compositions;
		
		return result;
	}
	
	public Element getXmlNode(Document doc){
		if(doc == null)
			return null;
		Element element = null;
		String layerType = "";
		switch (this.layerType) {
		case PIM:			
			layerType = "pimdata";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addPimDataAttributes(element);
			break;
		case PSM:
			layerType = "psmcontainers";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addPsmContainerAttributes(element);
			break;
		case ISM:
			layerType = "ismcontainers";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addIsmContainerAttributes(element);
			break;
		default:
			break;
		}
		
		return element;
	}
	
	private void addPimDataAttributes(Element data){
		//process synonyms
		String synonymName = "synonym";
		String synonymValue ="";
		for(String syn : this.synonyms){
			synonymValue += syn + " ";
		}
		data.setAttribute(synonymName, synonymValue);
		
		String associationNodeName = "dataAssoLinks";
		String associationTypeName = "assoType";
		
		//process aggregations
		Element aggregation = data.getOwnerDocument().createElement(associationNodeName);
		String associationType = "isAggregationOf";
		aggregation.setAttribute(associationTypeName, associationType);
		String associationDataName = "targetAssoData";
		String associationData = "";
		boolean existsAssociation = false;
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			associationData += "//@pims/@pimdata." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
		if(existsAssociation){
			aggregation.setAttribute(associationDataName, associationData);
			data.appendChild(aggregation);
		}
		
		// process compositions
		Element composition = data.getOwnerDocument().createElement(associationNodeName);
		associationType = "isCompositionOf";
		composition.setAttribute(associationTypeName, associationType);
		String compostionData = "";
		boolean existsComposition = false;
		for(DataContainerModel assoc : this.associations.get(AssociationType.COMPOSITION)){
			compostionData += "//@pims/@pimdata." + assoc.xmlPosition +" ";
			existsComposition = true;
		}
		if(existsComposition){
			composition.setAttribute(associationDataName, compostionData);
			data.appendChild(composition);
		}
		
		//process refinements
		String refinementAttribute = "storedin";
		String refinementData = "";
		boolean existsRefinement = false;
		for(DataContainerModel ref : this.refinements){
			String refLevel = "";
			if(ref.getLayerType().equals(LayerType.PIM))
				refLevel = "//@pims/@pimdata.";
			else if(ref.getLayerType().equals(LayerType.PSM))
				refLevel = "//@psms/@psmcontainers.";
			refinementData += refLevel + ref.xmlPosition +" ";
			existsRefinement = true;
		}
		if(existsRefinement)
			data.setAttribute(refinementAttribute, refinementData);
	}
	
	private void addPsmContainerAttributes(Element container){
		String associationAttribute = "containersassociation";
		String associationData = "";
		boolean existsAssociation = false;
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			associationData += "//@psms/@psmcontainers." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
		if(existsAssociation)
			container.setAttribute(associationAttribute, associationData);
		
		String refinementAttribute = "contimplementedas";
		String refinementData = "";
		boolean existsRefinement = false;
		for(DataContainerModel ref : this.refinements){
			String refLevel = "";
			if(ref.getLayerType().equals(LayerType.PSM))
				refLevel = "//@psms/@psmcontainers.";
			else if(ref.getLayerType().equals(LayerType.ISM))
				refLevel = "//@isms/@ismcontainers.";
			refinementData += refLevel + ref.xmlPosition +" ";
			existsRefinement = true;
		}
		if(existsRefinement)
			container.setAttribute(refinementAttribute, refinementData);
	}
	
	private void addIsmContainerAttributes(Element container){
		String associationAttribute = "implecontainerassociation";
		String associationData = "";
		boolean existsAssociation = false;
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			associationData += "//@isms/@ismcontainers." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
		if(existsAssociation)
			container.setAttribute(associationAttribute, associationData);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * The type must be the same.
	 * If the name is equal or there is a match between the name and the synonyms.
	 * This is used also by the a list when it verifies if an element is contained.
	 */
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof DataContainerModel))
			return false;
		DataContainerModel obj = (DataContainerModel) o;
		if(!this.layerType.equals(obj.layerType))
			return false; 
		if(this.name.equals(obj.name))
			return true;
		if(this.alsoKnownAs(obj.name))
			return true;
		if(obj.alsoKnownAs(this.name))
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		String unique = name + "#"+ this.layerType.name();
		return unique.hashCode();
	}
	
	/**
	 *
	 * There can be cases where there is a redundant implementation link which needs to be removed.
	 * <p>For example: Base model had a container "domElement" refined as "img" at the lower level.
	 *  The New model has the same container "domElement" refined at the same level with "media".	 *  
	 *  "Media" is refined at the same level as "img" and "img" is refined at the lower level with "img".
	 *  <p>
	 *  Before the filtering, "domElement" is refined as [ "img"-lower, "media"-same = ["img"-same"="img"-lower"]].
	 *  The problem is that "img"-lower is contained twice in the refinement.
	 *  After refinement only the refinement from "media" is left.
	 *
	 * @return number of removed elements
	 */
	public int trimRefinements(){
		boolean removed = true;
		int removedCounter = 0;
		DataContainerModel toRemove = null;
		while(removed){
			removed = false;
			for(DataContainerModel refCheck : this.refinements){
				for(DataContainerModel refSibling : this.refinements){
					if(refCheck.equals(refSibling))
						continue;
					removed = refSibling.containsRefinement(refCheck);
					if(removed){
						toRemove = refCheck;
						break;
					}
				}
				if(toRemove !=null)
					break;
			}
			if(toRemove!=null){
				this.refinements.remove(toRemove);
				toRemove = null;
				removed = true;
				removedCounter++;
			}
		}
		return removedCounter;
	}
	
	private boolean containsRefinement(DataContainerModel ref){
		boolean result = false;
		result = this.refinements.contains(ref);
		if(result)
			return true;
		for(DataContainerModel r : this.refinements){
			result = r.containsRefinement(ref);
			if(result)
				return true;
		}
		return false;
	}
}
