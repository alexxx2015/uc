package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import de.tum.in.i22.uc.adaptation.model.DomainModel.DomainLayer;

public class LayerModel {
	
	private String name;
	private DomainLayer type;
	
	public static final String indentation = "	";
	
	private ArrayList<SystemModel> systems;
	
	private ArrayList<DataContainerModel> dataContainers;
	private ArrayList<ActionTransformerModel> actionTransformers;
	
	private LayerModel refinedAs;
	
	public LayerModel(String name, DomainLayer type){
		this.name = name;
		this.type = type;
		systems = new ArrayList<>();
		dataContainers = new ArrayList<>();
		actionTransformers = new ArrayList<>();
	}
	
	public DataContainerModel getDataContainer(String name){
		for(DataContainerModel dc : this.dataContainers){
			if(dc.getName().equals(name))
				return dc;
		}
		return null;
	}
	
	public ArrayList<DataContainerModel> getDataContainers(){
		return this.dataContainers;
	}
	
	public LayerModel getRefinementLayer(){
		return this.refinedAs;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public DomainLayer getType(){
		return this.type;
	}
	
	public void setRefinedAs(LayerModel refinedAs){
		this.refinedAs = refinedAs;
	}
	
	public void addDataContainer(DataContainerModel dataContainer){
		this.dataContainers.add(dataContainer);
	}
	
	public void addActionTransformer(ActionTransformerModel actionTransformer){
		this.actionTransformers.add(actionTransformer);
	}
	
	public void addSystem(SystemModel system){
		this.systems.add(system);
	}
	
	public DataContainerModel getContainerAtPosition(int position){
		if(position >= this.dataContainers.size())
			return null;
		DataContainerModel result = this.dataContainers.get(position);
		return result;
	}
	
	public ActionTransformerModel getTransformerAtPosition(int position){
		if(position >= this.actionTransformers.size())
			return null;
		ActionTransformerModel result = this.actionTransformers.get(position);
		return result;
	}
	
	public SystemModel getSystemAtPosition(int position){
		if(position >= this.systems.size())
			return null;
		SystemModel result = this.systems.get(position);
		return result;
	}
	
	public String toString(){
		String result ="";
		result += this.indentation + name + ":" +type.name();
		
		String dataString = "";
		for(DataContainerModel data : this.dataContainers){
			dataString +="\n" + data.toString();
		}

		String actionsString = "";
		if(this.type.equals(DomainLayer.PIM)){
			for(ActionTransformerModel ac : this.actionTransformers){
				actionsString += "\n" + ac.toString();
			}
		}
		
		String systemString = "";
		for(SystemModel system : this.systems){
			systemString += "\n" + system.toString();
		}
		result += dataString;
		result += actionsString;
		result += systemString;
		return result;
	}
	
	public String toXMLString(){
		String result = "";
		String layerType = "";
		switch (this.type) {
		case PIM:
			layerType = "pims";
			break;
		case PSM:
			layerType = "psms";
		case ISM:
			layerType = "isms";
		default:
			break;
		}
		String startNode = "<"+ layerType +" name=" + "\"" + this.name + "\"" +">";
		String endNode = "</"+layerType+">";
		
		String containers = "";
		for(DataContainerModel dc : this.dataContainers){
			containers += "\n" + "		" + dc.toXMLString();
		}
		
		String transformers = "";
		//TODO:
		String systems = "";
		//TODO:
		
		result += "		" + startNode;
		result += "\n		"+ containers;
		result += "\n		"+ transformers;
		result += "\n		"+ systems;
		
		return result;
	}
}
