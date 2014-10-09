package de.tum.in.i22.uc.ptp.adaptation.domainmodel;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DomainModel.LayerType;

public class LayerModel {
	
	private String name;
	private LayerType type;
	
	public static final String indentation = "	";
	
	private ArrayList<SystemModel> systems;
	
	private ArrayList<DataContainerModel> dataContainers;
	private ArrayList<ActionTransformerModel> actionTransformers;
	
	private LayerModel refinedAs;
	private DomainModel parentDomainModel; 
	
	public LayerModel(String name, LayerType type){
		this.name = name;
		this.type = type;
		systems = new ArrayList<>();
		dataContainers = new ArrayList<>();
		actionTransformers = new ArrayList<>();
		this.parentDomainModel = null;
	}
	
	public void setParentDomainModel(DomainModel dm){
		this.parentDomainModel = dm;
	}
	
	/**
	 * Returns the number of elements defined at this layer.
	 * @return
	 */
	public int getElementsSize(){
		int containers = this.dataContainers.size();
		int transformers = this.actionTransformers.size();
		int systems = this.systems.size();
		int totalElements = containers + transformers + systems;
		return totalElements;
	}
	
	public DataContainerModel getDataContainer(String name){
		for(DataContainerModel dc : this.dataContainers){
			if(dc.getName().equals(name))
				return dc;
			if(dc.alsoKnownAs(name))
				return dc;
		}
		return null;
	}
	
	public DataContainerModel getDataContainer(DataContainerModel dc){
		for(DataContainerModel d : this.dataContainers){
			if(d.equals(dc))
				return d;
		}
		return null;
	}
	
	public ActionTransformerModel getActionTransformer(String name){
		for(ActionTransformerModel at : this.actionTransformers){
			if(at.getName().equals(name))
				return at;
			if(at.alsoKnownAs(name))
				return at;
		}
		return null;
	}
	
	public ActionTransformerModel getActionTransformer(ActionTransformerModel newAt){
		for(ActionTransformerModel at : this.actionTransformers){
			if(at.equals(newAt))
				return at;
		}
		return null;
	}
	
	public SystemModel getSystem(SystemModel system){
		for(SystemModel sys : this.systems){
			if(sys.equals(system))
				return sys;
		}
		return null;
	}
	
	/**
	 * Returns a list with all the Data or Container elements defined at this layer.
	 * @return
	 */
	public ArrayList<DataContainerModel> getDataContainers(){
		return this.dataContainers;
	}
	
	/**
	 * Returns a list with all the Action or Transformer elements
	 * defined at this layer regardless of the system.
	 * @return
	 */
	public ArrayList<ActionTransformerModel> getActionTransformers() {
		return this.actionTransformers;
	}
	
	public LayerModel getRefinementLayer(){
		return this.refinedAs;
	}
	
	/**
	 * Set the name of the layer.
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public LayerType getType(){
		return this.type;
	}
	
	/**
	 * Set the refinement layer of the current layer.
	 * @param refinedAs
	 */
	public void setRefinedAs(LayerModel refinedAs){
		this.refinedAs = refinedAs;
	}
	
	/**
	 * Add a Data or Container element to the list of elements defined at this level.
	 * @param dataContainer
	 */
	public void addDataContainer(DataContainerModel dataContainer){
		if(dataContainer == null){
			return;
		}
		int index = this.dataContainers.size();
		dataContainer.setXmlPosition(index);
		this.dataContainers.add(dataContainer);
	}
	
	/**
	 * Add an Action or a Transformer element to the list of elements defined at this level.
	 * @param actionTransformer
	 */
	public void addActionTransformer(ActionTransformerModel actionTransformer){
		if(actionTransformer == null){
			return;
		}
		int index = this.actionTransformers.size();
		actionTransformer.setXmlPosition(index);
		this.actionTransformers.add(actionTransformer);
		actionTransformer.setParenLayer(this);
	}
	
	/**
	 * Add a System element defined at this layer.
	 * System elements cannot be added at the PIM layer.
	 * @param system
	 */
	public void addSystem(SystemModel system){
		if(system == null){
			return;
		}
		if(this.type.equals(LayerType.PIM))
			return;
		int index = this.systems.size();
		system.setXmlPosition(index);
		this.systems.add(system);
	}
	
	/**
	 * Returns all the systems defined at this level.
	 * @return
	 */
	public ArrayList<SystemModel> getSystems(){
		return this.systems;
	}
	
	/**
	 * Returns an element at the position defined in the domain model.
	 * The position in the list is equivalent with the position 
	 * used by XPATH to access an element in the XML file.
	 * @param position
	 * @return
	 */
	public DataContainerModel getContainerAtPosition(int position){
		if(position >= this.dataContainers.size())
			return null;
		DataContainerModel result = this.dataContainers.get(position);
		return result;
	}
	
	/**
	 * Returns an element at the position defined in the domain model.
	 * The position in the list is equivalent with the position 
	 * used by XPATH to access an element in the XML file.
	 * @param position
	 * @return
	 */
	public ActionTransformerModel getTransformerAtPosition(int position){
		if(position >= this.actionTransformers.size())
			return null;
		ActionTransformerModel result = this.actionTransformers.get(position);
		return result;
	}
	
	/**
	 * Returns an element at the position defined in the domain model.
	 * The position in the list is equivalent with the position 
	 * used by XPATH to access an element in the XML file.
	 * @param position
	 * @return
	 */
	public SystemModel getSystemAtPosition(int position){
		if(position >= this.systems.size())
			return null;
		SystemModel result = this.systems.get(position);
		return result;
	}
	
	public String toString(){
		String result ="";
		result += this.indentation + name + ":" +type.name() +" "+ this.getElementsSize();
		
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
		result += "\n"+ this.indentation+"	DATA/CONTAINER" +" " + this.dataContainers.size() + dataString;
		result += "\n"+ this.indentation+"	ACTION/TRANSFORMER" +" "+ this.actionTransformers.size() + actionsString;
		result += "\n"+ this.indentation+"	SYSTEMS"+ " "+ this.systems.size()+ systemString;
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
		
		for(ActionTransformerModel at : this.actionTransformers){
			Element container = at.getXmlNode(doc);
			element.appendChild(container);
		}
		
		for(SystemModel sys : this.systems){
			Element container = sys.getXmlNode(doc);
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

	public void filterSystemRefinements(){
		for(SystemModel sys : systems)
			sys.trimRefinements();
	}

	public void filterActionTransformerSequences() {
		// TODO Auto-generated method stub
		
	}
	
}