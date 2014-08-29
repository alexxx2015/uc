package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.uc.adaptation.model.DomainModel.DomainLayerType;

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
	private ArrayList<String> aliases;
	
	private DomainLayerType type ;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
	private String indentationLevel ;
	
	private ArrayList<DataContainerModel> refinements;
	
	private Map<AssociationType, ArrayList<DataContainerModel>> associations;
	
	private LayerModel parentLayer;
	
	private boolean isMerged ;
	
	public DataContainerModel(String name, DomainLayerType type){
		this.name = name;
		this.type = type;
		this.xmlPosition = -1;
		this.isMerged = false;
		this.refinements = new ArrayList<>();
		aliases = new ArrayList<String>();
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
	
	public DomainLayerType getLayerType(){
		return this.type;
	}
	
	public ArrayList<DataContainerModel> getRefinements(){
		return this.refinements;
	}
	
	public DataContainerModel getRefinementByName(String name){
		if(name==null)
			return null;
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
	
	public void addAliasName(String alias){
		if(alias == null)
			return;
		if(this.name.equals(alias))
			return;
		if(!this.aliases.contains(alias))
			this.aliases.add(alias);
	}
	
	public boolean alsoKnownAs(String alias){
		if(alias == null)
			return false;
		return this.aliases.contains(alias);
	}
	
	public ArrayList<String> getAliases(){
		return this.aliases;
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
	
	public void addRefinement(DataContainerModel refinedAs){
		if(refinedAs == null)
			return;
		this.refinements.add(refinedAs);
	}
	
	/**
	 * Association is used at PSM and ISM layer.
	 * It is not specified if it aggregation or composition.
	 * By default, it is considered aggregation.
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
		result += this.indentationLevel+ name + " "+this.aliases +" - "+ type.name();
		
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
	
	public String toXMLString(){
		String result = "";
		switch(this.type){
			case PIM:
				result += toPimXmlRepresentation();
				break;
			case PSM:
				result += toPsmXmlRepresentation();
				break;
			case ISM:
				result += toIsmXmlRepresentation();
				break;
		}
		return result;
	}
	
	private String toPimXmlRepresentation(){
		String result = "";
		String startNode = "<pimdata ";
		String endNode = "</pimdata>";
		String nameAttribute = "name=\"" + this.name +"\" ";
		
		String aggregationData = "";
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			String association = "<dataAssoLinks assoType=\"isAggregationOf\" targetAssoData=\"//@pims/@pimdata.";
			association += assoc.xmlPosition + "/>";
			aggregationData += "\n" + this.indentationLevel +"	"+ association;
		}
		
		String compositionData = "";
		for(DataContainerModel assoc : this.associations.get(AssociationType.COMPOSITION)){
			String association = "<dataAssoLinks assoType=\"isAggregationOf\" targetAssoData=\"//@pims/@pimdata.";
			association += assoc.xmlPosition + "/>";
			compositionData += "\n" + this.indentationLevel +"	"+ association;
		}
		
		String refinementAttributeStart = "storedin=\"";
		String refinementData = "";
		for(DataContainerModel ref : this.refinements){
			refinementData += "//@psms/@psmcontainers." + ref.xmlPosition +" ";
		}
		String refinementAttributeEnd = "\"";
		
		result += startNode + nameAttribute 
				+ aggregationData  
				+ compositionData
				+ refinementAttributeStart + refinementData + refinementAttributeEnd
				+ endNode;
		return result;
	}
	
	private String toPsmXmlRepresentation(){
		String result = "";
		String startNode = "<psmcontainers ";
		String endNode = " />";
		String nameAttribute = "name=\"" + this.name +"\" ";
		
		String associationAttributeStart = "containersassociation=\"";
		String associationData = "";
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			associationData += "//@psms/@psmcontainers." + assoc.xmlPosition +" ";
		}
		String associationAttributeEnd = "\"";
		
		String refinementAttributeStart = "contimplementedas=\"";
		String refinementData = "";
		for(DataContainerModel ref : this.refinements){
			refinementData += "//@isms/@ismcontainers." + ref.xmlPosition +" ";
		}
		String refinementAttributeEnd = "\"";
		
		result += startNode + nameAttribute 
				+ associationAttributeStart + associationData + associationAttributeEnd 
				+ refinementAttributeStart + refinementData + refinementAttributeEnd
				+ endNode;
		return result;
	}
	
	private String toIsmXmlRepresentation(){
		String result = "";
		String startNode = "<ismcontainers ";
		String endNode = " />";
		String nameAttribute = "name=\"" + this.name +"\" ";
		String associationAttributeStart = "implecontainerassociation=\"";
		String associationData = "";
		for(DataContainerModel assoc : this.associations.get(AssociationType.AGGREGATION)){
			associationData += "//@isms/@ismcontainers." + assoc.xmlPosition +" ";
		}
		String associationAttributeEnd = "\"";
		result += startNode + nameAttribute 
				+ associationAttributeStart + associationData + associationAttributeEnd 
				+ endNode;
		return result;
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof DataContainerModel))
			return false;
		DataContainerModel obj = (DataContainerModel) o;
		boolean result = this.name.equals(obj.name) && this.type.equals(obj.type);
		return result;
	}
}
