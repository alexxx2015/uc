package de.tum.in.i22.uc.adaptation.model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.adaptation.model.DataContainerModel.AssociationType;
import de.tum.in.i22.uc.adaptation.model.DomainModel.LayerType;

public class SystemModel {

	private String name;
	
	/**
	 * PIM, PSM, ISM
	 */
	private LayerType layerType;
	/**
	 * This is used for the XPath processing.
	 */
	private int xmlPosition;
	private String indentationLevel ;
	
	private ArrayList<SystemModel> refinements ;
	
	/**
	 * Transformers.
	 */
	private ArrayList<ActionTransformerModel> operations;
	
	/**
	 * see ./doc/metamodel.png 
	 * <br> PSM - attribute systemassociation
	 * <br> ISM - attribute implesystemassociation
	 */
	private ArrayList<SystemModel> associations;
	
	private LayerModel parentLayer;
	
	private boolean isMerged ;
	
	public SystemModel(String name, LayerType type){
		this.name = name;
		this.layerType = type;
		refinements = new ArrayList<>();
		operations = new ArrayList<>();
		associations = new ArrayList<>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
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
	
	
	/**
	 * Association is an inner link to another system at the same abstraction layer.
	 */
	public void addAssociationLink(SystemModel system){
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
		refinements.add(system);
	}
	
	public void addOperation(ActionTransformerModel operation){
		if(operation == null)
			return;
		operations.add(operation);
	}
	
	public String toString(){
		String result = "";
		result += this.indentationLevel + name + ":" +layerType.name();
		
		String operations = "";
		for(ActionTransformerModel op : this.operations){
			operations += "\n" + this.indentationLevel + op.toString();
		}
		result += operations; 
		
		return result;
	}
	
	public String toStringShort(){
		String result ="";
		result += name +" {"+ layerType.name()+"}";
		return result;
	}
	
	public Element getXmlNode(Document doc){
		if(doc == null)
			return null;
		Element element = null;
		String layerType = "";
		switch (this.layerType) {
		case PIM: //there is no PIM for systems			
			break;
		case PSM:
			layerType = "psmsystems";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addPsmSystemAttributes(element);
			break;
		case ISM:
			layerType = "ismsystems";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			addIsmSystemAttributes(element);
			break;
		default:
			break;
		}
		
		return element;
	}
	
	private void addPsmSystemAttributes(Element element) {
		String transformersAttribute = "systemtransformers";
		String transformersData = "";
		boolean existsTransformers = false;
		for(ActionTransformerModel assoc : this.operations){
			transformersData += "//@psms/@psmtransformers." + assoc.getXmlPosition() +" ";
			existsTransformers = true;
		}
		
		String associationAttribute = "systemassociation";
		String associationData = "";
		boolean existsAssociation = false;
		for(SystemModel assoc : this.associations){
			associationData += "//@psms/@psmsystems." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
		
		String refinementAttribute = "sysimplementedas";
		String refinementData = "";
		boolean existsRefinement = false;
		for(SystemModel ref : this.refinements){
			String refLevel = "";
			if(ref.getLayerType().equals(LayerType.PSM))
				refLevel = "//@psms/@psmsystems.";
			else if(ref.getLayerType().equals(LayerType.ISM))
				refLevel = "//@isms/@ismsystems.";
			refinementData += refLevel + ref.xmlPosition +" ";
			existsRefinement = true;
		}
		
		if(existsTransformers)
			element.setAttribute(transformersAttribute, transformersData);
		if(existsRefinement)
			element.setAttribute(refinementAttribute, refinementData);
		if(existsAssociation)
			element.setAttribute(associationAttribute, associationData);
		
	}
	
	private void addIsmSystemAttributes(Element element) {
		String transformersAttribute = "implesystemtransformers";
		String transformersData = "";
		boolean existsTransformers = false;
		for(ActionTransformerModel assoc : this.operations){
			transformersData += "//@isms/@ismtransformers." + assoc.getXmlPosition() +" ";
			existsTransformers = true;
		}
		
		String associationAttribute = "implesystemassociation";
		String associationData = "";
		boolean existsAssociation = false;
		for(SystemModel assoc : this.associations){
			associationData += "//@isms/@ismsystems." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
		
		if(existsTransformers)
			element.setAttribute(transformersAttribute, transformersData);
		//there is no cross refinement for ISM systems.
		if(existsAssociation)
			element.setAttribute(associationAttribute, associationData);
		
	}

	public boolean equals(Object o){
		if (o == null)
			return false;
		if(! (o instanceof SystemModel))
			return false;
		SystemModel obj = (SystemModel) o;
		boolean result = this.name.equals(obj.name)
					&& (this.layerType.equals(obj.layerType))
					;
		return result;
	}

	@Override
	public int hashCode() {
		String unique = name + "#"+ this.layerType.name();
		return unique.hashCode();
	}
	
	public String getName() {
		return this.name;
	}

	public LayerType getLayerType() {
		return this.layerType;
	}

	public ArrayList<SystemModel> getRefinements() {
		return this.refinements;
	}

	public SystemModel getRefinement(SystemModel sys) {
		for(SystemModel ref : this.refinements){
			if(ref.equals(sys))
				return ref;
		}
		return null;
	}

	public SystemModel getAssociationLink(SystemModel assoc) {
		for(SystemModel a : this.associations){
			if(a.equals(assoc))
				return a;
		}
		return null;
	}
	
	/**
	 * @return number of removed elements
	 */
	public int trimRefinements(){
		boolean removed = true;
		int removedCounter = 0;
		SystemModel toRemove = null;
		while(removed){
			removed = false;
			for(SystemModel refCheck : this.refinements){
				for(SystemModel refSibling : this.refinements){
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
	
	private boolean containsRefinement(SystemModel ref){
		boolean result = false;
		result = this.refinements.contains(ref);
		if(result)
			return true;
		for(SystemModel r : this.refinements){
			result = r.containsRefinement(ref);
			if(result)
				return true;
		}
		return false;
	}
	
}
