package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

public class LayerModel {
	
	private String name;
	private LayerType type;
	
	public static final String indentation = "	";
	
	private ArrayList<SystemModel> systems;
	
	private ArrayList<DataContainerModel> dataContainers;
	private ArrayList<ActionTransformerModel> actionTransformers;
	
	private LayerModel refinedAs;
	
	public LayerModel(String name, LayerType type){
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
	
	public LayerType getType(){
		return this.type;
	}
	
	public void setRefinedAs(LayerModel refinedAs){
		this.refinedAs = refinedAs;
	}
	
	public void addDataContainer(DataContainerModel dataContainer){
		if(dataContainer == null){
			return;
		}
		int index = this.dataContainers.size();
		dataContainer.setXmlPosition(index);
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
		//if(this.type.equals(LayerType.PIM)){
			for(ActionTransformerModel ac : this.actionTransformers){
				actionsString += "\n" + ac.toString();
			}
		
		
		
		String systemString = "";
		for(SystemModel system : this.systems){
			systemString += "\n" + system.toString();
		}
		result += "\n"+ this.indentation+"DATA/CONTAINER" + dataString;
		result += "\n"+ this.indentation+"ACTION/TRANSFORMER" + actionsString;
		result += "\n"+ this.indentation+"SYSTEMS"+ systemString;
		return result;
	}
	
	public Element getXmlNode(Document doc){
		if(doc == null)
			return null;
		String layerType = "";
		switch (this.type) {
		case PIM:
			layerType = "pims";
			break;
		case PSM:
			layerType = "psms";
			break;
		case ISM:
			layerType = "isms";
			break;
		default:
			break;
		}
		Element element = doc.createElement(layerType);
		element.setAttribute("name", this.name);
		
		for(DataContainerModel dc : this.dataContainers){
			Element container = dc.getXmlNode(doc);
			element.appendChild(container);
		}
		
		return element;
	}
	
	/**
	 * There can be cases where there is a redundant implementation link which needs to be removed.
	 * <p>For example: Base model had a container "domElement" refined as "img" at the lower level.
	 *  The New model has the same container "domElement" refined at the same level with "media".	 *  
	 *  "Media" is refined at the same level as "img" and "img" is refined at the lower level with "img".
	 *  <p>
	 *  Before the filtering, "domElement" is refined as [ "img"-lower, "media"-same = ["img"-same"="img"-lower"]].
	 *  The problem is that "img"-lower is contained twice in the refinement.
	 *  After refinement only the refinement from "media" is left.
	 */
	public void filterDataContainerRefinements(){
		for(DataContainerModel dc : dataContainers)
			dc.trimRefinements();
	}
}
