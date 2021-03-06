package de.tum.in.i22.uc.ptp.adaptation.domainmodel;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tum.in.i22.uc.ptp.adaptation.domainmodel.DomainModel.LayerType;

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
		SEQ,
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
	private boolean innerLayerRefined;
	
	private ArrayList<ActionTransformerModel> innerRefinements;
	private ArrayList<ActionTransformerModel> crossRefinements;
	private ArrayList<ActionTransformerModel> specializations;
	
	private ArrayList<ActionTransformerModel> associations;
	
	/**
	 * PIM - equivalent of paramData
	 */
	private ArrayList<DataContainerModel> inputParams;
	
	private ArrayList<DataContainerModel> outputParams;
	
	private LayerModel parentLayer;
	
	private SystemModel parentSystem;
	
	private boolean isMerged ;
	
	/**
	 * To be removed from the PTP implementation  and the DomainModel.
	 */
	@Deprecated
	private int sequenceIndex;
	
	public ActionTransformerModel(String name, LayerType type){
		this.name = name;
		this.layerType = type;
		this.innerRefinements = new ArrayList<>();
		this.crossRefinements = new ArrayList<>();
		this.specializations = new ArrayList<>();
		this.inputParams = new ArrayList<>();
		this.outputParams = new ArrayList<>();
		this.associations = new ArrayList<>();
		this.synonyms = new ArrayList<String>();
		this.indentationLevel = "";
		this.xmlPosition = -1;
		this.isMerged = false;
		this.parentSystem = null;
		this.refinementType = RefinementType.SET;
		this.setSequenceIndex(0);
		this.innerLayerRefined = false;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Planned to be removed from the PTP and the DomainModel.
	 * @return the sequenceIndex
	 */
	@Deprecated
	public int getSequenceIndex() {
		return sequenceIndex;
	}

	/**
	 * Planned to be removed from the PTP and the DomainModel.
	 * @param sequenceIndex the sequenceIndex to set
	 */
	@Deprecated
	public void setSequenceIndex(int sequenceIndex) {
		this.sequenceIndex = sequenceIndex;
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
	
	public RefinementType getInnerRefinementType(){
		return this.refinementType;
	}
	
	public boolean addSynonym(String name){
		if(name==null)
			return false;
		if(name.equals(""))
			return false;
		if(this.name.equals(name))
			return false;
		if(!this.synonyms.contains(name))
			return this.synonyms.add(name);
		return false;
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
	
	public ArrayList<ActionTransformerModel> getInnerRefinements(){
		return this.innerRefinements;
	}
	
	public ArrayList<ActionTransformerModel> getCrossRefinements(){
		return this.crossRefinements;
	}
	
	public ArrayList<ActionTransformerModel> getSpecializations(){
		return this.specializations;
	}
	
	public void addInnerRefinement(ActionTransformerModel refinedAs){
		if(refinedAs == null)
			return;
		if(this.refinementType.equals(RefinementType.SEQ)){
			int index = this.innerRefinements.size();
			refinedAs.sequenceIndex = index;
		}
		this.innerRefinements.add(refinedAs);
	}
	
	public void addCrossRefinement(ActionTransformerModel refinedAs){
		if(refinedAs == null)
			return;
		this.crossRefinements.add(refinedAs);
	}
	
	public void addSpecialization(ActionTransformerModel spec){
		if(spec == null)
			return;
		this.specializations.add(spec);
	}
	
//	/**
//	 * There is no inner refinement for actions defined at PIM.
//	 * @param name
//	 * @return
//	 */
//	public ActionTransformerModel getInnerRefinementByName(String name){
//		if(name==null)
//			return null;		
//		for(ActionTransformerModel ref : this.innerRefinements){
//			if(ref.name.equals(name))
//				return ref;
//			if(ref.alsoKnownAs(name))
//				return ref;
//		}
//		return null;
//	}
	
	public ActionTransformerModel getInnerRefinement(ActionTransformerModel a){
		if(a==null)
			return null;		
		for(ActionTransformerModel ref : this.innerRefinements){
			if(ref.equals(a))
				return ref;
		}
		return null;
	}
	
	public ActionTransformerModel getCrossRefinement(ActionTransformerModel a){
		if(a==null)
			return null;		
		for(ActionTransformerModel ref : this.crossRefinements){
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
		result += this.indentationLevel+name +" "+this.getSignature()+" "+ this.refinementType.name() +" sys: "+ systemName;
		String refinedAs = " inner";
		for(ActionTransformerModel ref : this.innerRefinements){
			refinedAs += " "+ ref.name; 
		}
		refinedAs += " cross";
		for(ActionTransformerModel ref : this.crossRefinements){
			refinedAs += " "+ ref.name +"-"+ ref.layerType.name(); 
		}
		result += refinedAs;
		return result;
	}

	private String getSignature(){
		String sig = "[ ";
		for(DataContainerModel in : inputParams){
			sig += in.getName() +" ";
		}
		sig +="]";
		sig +="->[ ";
		for(DataContainerModel in : outputParams){
			sig += in.getName() +" ";
		}
		sig +="]";
		return sig;
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
			layerType = "pimactions";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			element.setAttribute("pos", ""+this.xmlPosition);
			addPimActionAttributes(element);
			break;
		case PSM:
			layerType = "psmtransformers";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			if (this.refinementType.equals(RefinementType.SEQ)){
				element.setAttribute("refType", "seqRefmnt");
			}
			element.setAttribute("seq", ""+sequenceIndex);
			//TODO: used only for reading/debugging
			element.setAttribute("system", this.parentSystem.getName());
			element.setAttribute("pos", ""+this.xmlPosition);
			addPsmTransformerAttributes(element);
			break;
		case ISM:
			layerType = "ismtransformers";
			element = doc.createElement(layerType);
			element.setAttribute("name", this.name);
			if (this.refinementType.equals(RefinementType.SEQ)){
				element.setAttribute("refType", "seqRefmnt");
			}
			//TODO: used only for reading/debugging
			element.setAttribute("system", this.parentSystem.getName());
			element.setAttribute("pos", ""+this.xmlPosition);
			element.setAttribute("seq", ""+sequenceIndex);
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
			associationData += "//@pims/@pimactions." + assoc.xmlPosition +" ";
			existsAssociation = true;
		}
	
		//process refinements
		String refinementAttribute = "actionRefmnt";
		String refinementData = "";
		boolean existsRefinement = false;
		for(ActionTransformerModel ref : this.crossRefinements){
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
			paramData += refLevel + p.getXmlPosition() +" ";
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
	
	private void addPsmTransformerAttributes(Element action){
		//process synonyms
		String synonymName = "synonym";
		String synonymValue ="";
		for(String syn : this.synonyms){
			synonymValue += syn + " ";
		}
		
		//process inner refinements
		String refinementInnerAttribute = "psmRefmnt";
		String refinementInnerData = "";
		boolean existsInnerRefinement = false;
		for(ActionTransformerModel ref : this.innerRefinements){
			if(ref.layerType.equals(this.layerType)){
				String refLevel = "//@psms/@psmtransformers.";
				refinementInnerData += refLevel + ref.xmlPosition +" ";
				existsInnerRefinement = true;
			}
		}
		
		//process cross refinements
		String refinementCrossAttribute = "crossPsmRefmnt";
		String refinementCrossData = "";
		boolean existsCrossRefinement = false;
		for(ActionTransformerModel ref : this.crossRefinements){
			if(!ref.layerType.equals(this.layerType)){
				String refLevel = "//@isms/@ismtransformers.";
				refinementCrossData += refLevel + ref.xmlPosition +" ";
				existsCrossRefinement = true;
			}
		}
		
		//process input param
		String inputParamAttribute = "inputcontainer";
		String inputParamData = "";
		for(DataContainerModel in : this.inputParams){
				String refLevel = "//@psms/@psmcontainers.";
				inputParamData += refLevel + in.getXmlPosition() +" ";
		}
		
		//process output param
		String outputParamAttribute = "outputcontainer";
		String outputParamData = "";
		for(DataContainerModel in : this.outputParams){
				String refLevel = "//@psms/@psmcontainers.";
				outputParamData += refLevel + in.getXmlPosition() +" ";
		}		
		
		if(existsInnerRefinement)
			action.setAttribute(refinementInnerAttribute, refinementInnerData);
		if(existsCrossRefinement)
			action.setAttribute(refinementCrossAttribute, refinementCrossData);
		
		action.setAttribute(synonymName, synonymValue);
		action.setAttribute(inputParamAttribute, inputParamData);
		action.setAttribute(outputParamAttribute, outputParamData);
	}
	
	private void addIsmTransformerAttributes(Element action){
		//process synonyms
		String synonymName = "synonym";
		String synonymValue ="";
		for(String syn : this.synonyms){
			synonymValue += syn + " ";
		}
		
		//process inner refinements
		String refinementInnerAttribute = "ismRefmnt";
		String refinementInnerData = "";
		boolean existsInnerRefinement = false;
		for(ActionTransformerModel ref : this.innerRefinements){
			if(ref.layerType.equals(this.layerType)){
				String refLevel = "//@isms/@ismtransformers.";
				refinementInnerData += refLevel + ref.xmlPosition +" ";
				existsInnerRefinement = true;
			}
		}
		
		//there is no cross refinement at ISM
		
		//process input param
		String inputParamAttribute = "inputimplecontainer";
		String inputParamData = "";
		for(DataContainerModel in : this.inputParams){
				String refLevel = "//@isms/@ismcontainers.";
				inputParamData += refLevel + in.getXmlPosition() +" ";
		}
		
		//process output param
		String outputParamAttribute = "outputimplecontainer";
		String outputParamData = "";
		for(DataContainerModel in : this.outputParams){
				String refLevel = "//@isms/@ismcontainers.";
				outputParamData += refLevel + in.getXmlPosition() +" ";
		}	
		
		if(existsInnerRefinement)
			action.setAttribute(refinementInnerAttribute, refinementInnerData);
		action.setAttribute(synonymName, synonymValue);
		action.setAttribute(inputParamAttribute, inputParamData);
		action.setAttribute(outputParamAttribute, outputParamData);
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

	/**
	 * Uses the equals function + the signature. Verifies
	 * <br> name
	 * <br> synonyms
	 * <br> system
	 * <br> signature
	 * @param o
	 * @return
	 */
	public boolean equivalent(ActionTransformerModel o){
		if(o == null)
			return false;
		if(!o.equals(this))
			return false;
		if(!equivalentTransformerSignature(o, this))
			return false;
		return true;
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
		this.innerRefinements.clear();
	}

	/**
	 * Checks the signature of the compared transformers.
	 * <br> Two transformers have the same signature if:
	 * <br> - they have the same number of input elements AND
	 * <br> - they have the same input params in the same order
	 * <br> The output parameters are replaced from new to the base in case of a match.
	 * The replacement is done at the merging.
	 * @param newAt
	 * @param baseE
	 * @return boolean - true for same signature, false for different signature
	 */
	public static boolean equivalentTransformerSignature(ActionTransformerModel newAt, ActionTransformerModel baseE) {

		ArrayList<DataContainerModel> newSignature = newAt.getInputParams();
		ArrayList<DataContainerModel> baseSignature = baseE.getInputParams();
		if(newSignature.size()!=baseSignature.size()){
			return false;
		}
		for(int i=0; i<newSignature.size(); i++){
			DataContainerModel newIn = newSignature.get(i);
			DataContainerModel baseIn = baseSignature.get(i);
			if(!newIn.equals(baseIn))
				return false;
		}
		
		return true;
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

	public void resetOutputParam() {
		this.outputParams.clear();
	}
}
