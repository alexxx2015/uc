package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

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
	
	private ArrayList<ActionTransformerModel> operations;
	
	private SystemModel parentModel;
	private LayerModel parentLayer;
	
	public SystemModel(String name, LayerType type){
		this.name = name;
		this.type = type;
		refinedAs = new ArrayList<>();
		operations = new ArrayList<>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
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
