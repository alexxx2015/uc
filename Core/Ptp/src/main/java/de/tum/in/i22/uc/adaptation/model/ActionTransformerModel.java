package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

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
	
	/**
	 *
	 */
	public static enum AuthorizationType{
		READ,
		ADD,
		MODIFY
	}
	
	private String name;
	/**
	 * After merging, there can be the same action with multiple similar names.
	 * To avoid duplicating the entry, a synonym list is kept.
	 */
	private ArrayList<String> synonyms;
	
	private LayerType layerType;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
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
	
	public ActionTransformerModel(String name, LayerType type){
		this.name = name;
		this.layerType = type;
		refinements = new ArrayList<>();
		inputParams = new ArrayList<>();
		outputParams = new ArrayList<>();
		synonyms = new ArrayList<String>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
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
		if(!this.synonyms.contains(name))
			this.synonyms.add(name);
	}
	
	public boolean alsoKnownAs(String synonym){
		return this.synonyms.contains(synonym);
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
	
	public void addRefinement(ActionTransformerModel refinedAs){
		if(refinedAs == null)
			return;
		this.refinements.add(refinedAs);
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

	public String toXMLString(){
		//TODO: add action xml representation
		return "";
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if(! (o instanceof ActionTransformerModel))
			return false;
		ActionTransformerModel obj = (ActionTransformerModel) o;
		boolean result = this.name.equals(obj.name)
					&& (this.layerType.equals(obj.layerType))
					&& (this.parentSystem.equals(obj.parentSystem))
					;
		return result;
	}
}
