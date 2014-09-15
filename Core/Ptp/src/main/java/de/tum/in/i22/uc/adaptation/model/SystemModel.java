package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import de.tum.in.i22.uc.adaptation.model.DataContainerModel.AssociationType;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

public class SystemModel {

	private String name;
	
	/**
	 * PIM, PSM, ISM
	 */
	private LayerType type;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
	private String indentationLevel ;
	
	private ArrayList<SystemModel> refinedAs ;
	
	/**
	 * Transformers.
	 */
	private ArrayList<ActionTransformerModel> operations;
	
	private ArrayList<SystemModel> associations;
	
	private SystemModel parentModel;
	private LayerModel parentLayer;
	
	private boolean isMerged ;
	
	public SystemModel(String name, LayerType type){
		this.name = name;
		this.type = type;
		refinedAs = new ArrayList<>();
		operations = new ArrayList<>();
		associations = new ArrayList<>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
		this.parentModel = null;
		this.parentLayer = null;
	}
	
	public void markAsMerged(){
		this.isMerged = true;
	}
	
	public boolean isMerged(){
		return this.isMerged;
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
	
	public void setParentSystemModel(SystemModel parentSystem){
		this.parentModel = parentSystem;
	}
	
	/**
	 * Association is an inner link to another system at the same abstraction layer.
	 */
	public void addAssociation(SystemModel system){
		if(system == null)
			return;
		this.associations.add(system);
	}
	
	public ArrayList<SystemModel> getAssociations(){
		return this.associations;
	}
	
	public void addRefinement(SystemModel system){
		if(system == null)
			return;
		refinedAs.add(system);
	}
	
	public void addOperation(ActionTransformerModel operation){
		if(operation == null)
			return;
		operations.add(operation);
	}
	
	public String toString(){
		String result = "";
		result += this.indentationLevel + name + ":" +type.name();
		
		String operations = "";
		for(ActionTransformerModel op : this.operations){
			operations += "\n" + this.indentationLevel + op.toString();
		}
		result += operations; 
		
		return result;
	}
	
	public String toXMLString(){
		//TODO: add system xml representation
		return "";
	}
	
	public boolean equals(Object o){
		if (o == null)
			return false;
		if(! (o instanceof SystemModel))
			return false;
		SystemModel obj = (SystemModel) o;
		boolean result = this.name.equals(obj.name)
					&& (this.type.equals(obj.type))
					;
		return result;
	}
}
