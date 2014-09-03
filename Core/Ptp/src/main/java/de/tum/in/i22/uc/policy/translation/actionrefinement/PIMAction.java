package de.tum.in.i22.uc.policy.translation.actionrefinement;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tum.in.i22.uc.policy.classes.Resource;

/**
 * 14 April 2013
 * Actions are at the PIM layer
 * 
 * @author ELIJAH
 */
public class PIMAction extends Event {
	
	/**
	 * Creates an instance of a PIMAction
	 */
	public PIMAction(){
		super();
	}
	
	/**
	 * Create a new PIMAction instance
	 * 
	 * @param name
	 * @param seqNumber
	 * @param refinementType
	 */
	public PIMAction(String name, int seqNumber, String refinementType){
		super(name,seqNumber,"PIM Action",refinementType);
		//
		this.oParentEvent=null;
	}
		
	
	/**
	 * Get a matching action in domain file. If it happens, we build a fresh
	 * action object and return it for refinement
	 * 
	 * @param resource
	 * @return Returns a new PIMAction on which we can call refinement tau().
	 * @throws ActionMatchingException 
	 */
	private static PIMAction getMatchingDomainFileAction(Resource resource) throws ActionMatchingException  {
		PIMAction action=null;
		Node nodePIMAction=null;
		
		//Do we have an action with the given name in our model file?
		String sExpression="//pimactions[@name='"+resource.getExtraInformation().toString()+"']";
		nodePIMAction=ActionRefinement.processXpathExpression(sExpression).item(0);
		if(nodePIMAction!=null){
			
			//we now have a match but do we have a matching data?
			sExpression="//pimdata[@name='"+resource.getDataClass()+"']";
			Node nodePIMData=null;
			nodePIMData=ActionRefinement.processXpathExpression(sExpression).item(0);			
			
			//check for synonyms of that data
			if(nodePIMData==null){
				sExpression="//pimdata[contains(@synonym,'"+resource.getDataClass()+"']";
				nodePIMData=ActionRefinement.processXpathExpression(sExpression).item(0);
			}
			
			if(nodePIMData!=null){
				//now we really have a match :)
				//so prepare a concrete action object	
				
				//PIMData can be an album, a composite or aggregate of
				//photos or a single photo				
				Node nodePIMAssociation=nodePIMData.getChildNodes().item(1);				
				
				String sName=nodePIMAction.getAttributes().getNamedItem("name").getNodeValue();									
				//
				int iSeqNumber=-1;	
				Node ndSequence=nodePIMAction.getAttributes().getNamedItem("seq");
				if(ndSequence!=null) iSeqNumber=Integer.valueOf(ndSequence.getNodeValue());
				//
				String sRefinementType="setRefmnt";		//default if not existing
				Node ndRefType=nodePIMAction.getAttributes().getNamedItem("refType");
				if(ndRefType!=null)
					sRefinementType=nodePIMAction.getAttributes().getNamedItem("refType").getNodeValue();
				
				action=new PIMAction(sName,iSeqNumber,sRefinementType);				
				
				//check PIM data parameteres
				Node nPimActionParamData = nodePIMAction.getAttributes().getNamedItem("paramData");
				String[] sPimActionParamData = nPimActionParamData.getNodeValue().split(" ");
				for(String paramData : sPimActionParamData){
					sExpression = ActionRefinement.getNewXPathAddress(paramData);	
					NodeList data = ActionRefinement.processXpathExpression(sExpression);
					Node dataNode = data.item(0);
					if(dataNode == null)					
							throw new ActionMatchingException(" Action {"+resource.getExtraInformation().toString()+"} exists, the paramData="+ paramData+" is missing");
					
				}
				
				//set other required properties
				//try to get cross transformers
				String sTransformers="";
				boolean bHasInTransformers=false;
				Node ndChildTransformers=nodePIMAction.getAttributes().getNamedItem("actionRefmnt");
				if(ndChildTransformers!=null)	{
					sTransformers = ndChildTransformers.getNodeValue();	
					bHasInTransformers=false;
				}
				//if there are no cross pim transformers (actionRefmnt), then there are inner pim transformers (actionassociation)
				if(sTransformers.equals("")){
					Node nodeInnerTransformers=nodePIMAction.getAttributes().getNamedItem("actionassociation");
					if(nodeInnerTransformers!=null) {
						sTransformers=nodeInnerTransformers.getNodeValue();
						bHasInTransformers=true;
					}
				}	
				//Prepare the data associated with this action
				//It was the basis for our condition above	
				action.setTransformersPath(sTransformers);
				//action.prepareActionData(nodePIMData, nodePIMAssociation);
				action.setHasInnerTransformers(bHasInTransformers);
			} else
			
					throw new ActionMatchingException(" Action {"+resource.getExtraInformation().toString()+"} exists but not for data {"+resource.getDataClass()+"}");
				          
	}
			
		return action;
	}
	
	
	/**
	 * Attempt to first get a matching action name & data class
	 * 
	 * @param resource
	 * @return
	 * @throws XPathExpressionException
	 * @throws DOMException
	 * @throws ActionMatchingException 
	 */
	public static PIMAction createAction(Resource resource) throws XPathExpressionException, DOMException, ActionMatchingException{				
		if(resource!=null)	return getMatchingDomainFileAction(resource);
		else return null;
	}			
	
		
	/**
	 * White-space separated xpath addresses to
	 * child PSM transformers
	 */
	private String sPSMTransformersXPath;
	/**
	 * Set the transformers XPath
	 * 
	 * @param transformersPath
	 */
	public void setTransformersPath(String transformersPath){
		this.sPSMTransformersXPath=transformersPath;
	}
	/**
	 * Prepare PSM Transformers
	 * 
	 * @return Returns an array of PSM Transformers
	 */
	private PSMTransformer[] preparePSMTransformers(){
		PSMTransformer[] arrTransformers=null;
		
		//1st step:
		if(sPSMTransformersXPath!=null && !sPSMTransformersXPath.equals("") ){
			
			String[] sArrTransformer=sPSMTransformersXPath.split(" ");
			arrTransformers=new PSMTransformer[sArrTransformer.length];
			
			for(int i=0; i<sArrTransformer.length; ++i){
				
				//2nd step: refine xpath expression
				String sExpression=ActionRefinement.getNewXPathAddress(sArrTransformer[i]);				
				Node nodeTransformer=ActionRefinement.processXpathExpression(sExpression).item(0);
				//3rd step: prepare fields for transformer
				String sName=nodeTransformer.getAttributes().getNamedItem("name").getNodeValue();
				int iSeq=Integer.valueOf(nodeTransformer.getAttributes().getNamedItem("seq").getNodeValue());
				String sRefinementType="setRefmnt";
				Node nodeRefmnt=nodeTransformer.getAttributes().getNamedItem("refType");
				if(nodeRefmnt!=null) sRefinementType=nodeRefmnt.getNodeValue();
				String sInputContainerXPath="";
				Node nodeInputContainer=nodeTransformer.getAttributes().getNamedItem("inputcontainer");
				if(nodeInputContainer!=null) sInputContainerXPath=nodeInputContainer.getNodeValue();
				String sOutputContainerXPath="";
				Node nodeOutputContainer=nodeTransformer.getAttributes().getNamedItem("outputcontainer");
				if(nodeOutputContainer!=null) sOutputContainerXPath=nodeOutputContainer.getNodeValue();
				//try to get cross transformers
				String sTransformers="";
				boolean bHasInTransformers=false;
				Node nodeCrossTransformers=nodeTransformer.getAttributes().getNamedItem("crossPsmRefmnt");
				if(nodeCrossTransformers!=null) {
					sTransformers=nodeCrossTransformers.getNodeValue();
					bHasInTransformers=false;
				}
				//if there are no cross psm transformers, then there are inner psm transformers
				if(sTransformers.equals("")){
					Node nodeInnerTransformers=nodeTransformer.getAttributes().getNamedItem("psmRefmnt");
					if(nodeInnerTransformers!=null) {
						sTransformers=nodeInnerTransformers.getNodeValue();
						bHasInTransformers=true;
					}
				}
				//4th step: prepare our transformer. Since it is first level, we set it to null
				PSMTransformer transformer=new PSMTransformer(sName,iSeq,sRefinementType,null);
				transformer.setInputContainerXPath(sInputContainerXPath);
				transformer.setOutputContainerXPath(sOutputContainerXPath);
				transformer.setTransformers(sTransformers);
				transformer.setHasInnerTransformers(bHasInTransformers);
				transformer.setParentContainer(null);
				arrTransformers[i]=transformer;			
			}
		}
		return arrTransformers;
	}
	
	/**
	 * Prepare PIM Actions
	 * 
	 * @return Returns an array of PSM Transformers
	 */
	private PIMAction[] preparePIMActions(){
		PIMAction[] arrTransformers=null;
		
		//1st step:
		if(sPSMTransformersXPath!=null && !sPSMTransformersXPath.equals("") ){
			
			String[] sArrTransformer=sPSMTransformersXPath.split(" ");
			arrTransformers=new PIMAction[sArrTransformer.length];
			
			for(int i=0; i<sArrTransformer.length; ++i){
				
				//2nd step: refine xpath expression
				String sExpression=ActionRefinement.getNewXPathAddress(sArrTransformer[i]);				
				Node nodeTransformer=ActionRefinement.processXpathExpression(sExpression).item(0);
				//3rd step: prepare fields for transformer
				String sName=nodeTransformer.getAttributes().getNamedItem("name").getNodeValue();
				int iSeq=Integer.valueOf(nodeTransformer.getAttributes().getNamedItem("seq").getNodeValue());
				String sRefinementType="setRefmnt";
				Node nodeRefmnt=nodeTransformer.getAttributes().getNamedItem("refType");
				if(nodeRefmnt!=null) sRefinementType=nodeRefmnt.getNodeValue();
				
				//check PIM data parameters
				Node nPimActionParamData = nodeTransformer.getAttributes().getNamedItem("paramData");
				String[] sPimActionParamData = nPimActionParamData.getNodeValue().split(" ");
				for(String paramData : sPimActionParamData){
					sExpression = ActionRefinement.getNewXPathAddress(paramData);	
					NodeList data = ActionRefinement.processXpathExpression(sExpression);
					Node dataNode = data.item(0);
					if(dataNode == null)
						return null; //cancel the inner refinement
				}
				
				//try to get cross transformers
				String sTransformers="";
				boolean bHasInTransformers=false;
				Node nodeCrossTransformers=nodeTransformer.getAttributes().getNamedItem("crossPsmRefmnt");
				if(nodeCrossTransformers!=null) {
					sTransformers=nodeCrossTransformers.getNodeValue();
					bHasInTransformers=false;
				}
				//if there are no cross psm transformers, then there are inner psm transformers
				if(sTransformers.equals("")){
					Node nodeInnerTransformers=nodeTransformer.getAttributes().getNamedItem("psmRefmnt");
					if(nodeInnerTransformers!=null) {
						sTransformers=nodeInnerTransformers.getNodeValue();
						bHasInTransformers=true;
					}
				}
				
				//4th step: prepare our transformer. Since it is first level, we set it to null
				PIMAction transformer=new PIMAction(sName,iSeq,sRefinementType);
				transformer.setTransformersPath(sTransformers);
				transformer.setHasInnerTransformers(bHasInTransformers);
				//transformer.prepareActionData(nodePIMData, nodePIMAssociation);
				arrTransformers[i]=transformer;			
			}
		}
		return arrTransformers;
	}
	
		
	/**
	 * Does cross refinement.
	 */
	private void crossSetRefinement(){
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!
//		ArrayList<PSMTransformer> alTrans=applicableTransformers();
//		PSMTransformer[] arrTransformers=new PSMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new PSMTransformer[alTrans.size()]);
		
		PSMTransformer[] arrTransformers = preparePSMTransformers();
		//update count of applicable transformers
		this.iNumApplicableCrossTransformers=arrTransformers.length;
		
		if(arrTransformers.length>1){
			
			for(int i=0; i<arrTransformers.length; ++i){	
				
				if(i<arrTransformers.length-1){
					ActionRefinement.createXmlResult(Reason.SET_PROCESSING, null,null);
				}
				arrTransformers[i].oParentEvent=this;
				arrTransformers[i].iOutputSequence=i;
				arrTransformers[i].tau();							
			}
			ActionRefinement.createXmlResult(Reason.SET_END_PROCESSING, null, arrTransformers.length-1);
		}
		else if(arrTransformers.length==1){
			arrTransformers[0].oParentEvent=this;
			arrTransformers[0].tau();
		}
		else{
			ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER, this, null);
		}
	}	
	
	
	/**
	 * Does sequence refinement.
	 */
	private void crossSeqRefinement(){		
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!
//		ArrayList<PSMTransformer> alTrans=applicableTransformers();
//		PSMTransformer[] arrTransformers=new PSMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new PSMTransformer[alTrans.size()]);
		
		PSMTransformer[] arrTransformers;
		arrTransformers = preparePSMTransformers();
		//update count of applicable transformers
		this.iNumApplicableCrossTransformers=arrTransformers.length;
				
		Arrays.sort(arrTransformers);
		
		if(arrTransformers.length>1){
			//arrange output
			ActionRefinement.createXmlResult(Reason.SEQUENCE_START, null,null);
			//looping
			for(int i=arrTransformers.length-1; i>=0; --i){
				arrTransformers[i].oParentEvent=this;
				arrTransformers[i].iOutputSequence=i;
				arrTransformers[i].tau();
				ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER_IN_SEQ, arrTransformers[i], null );
			}
			ActionRefinement.createXmlResult(Reason.SEQUENCE_END, null,arrTransformers.length);
		}
		else if(arrTransformers.length==1){
			arrTransformers[0].oParentEvent=this;
			arrTransformers[0].tau();
		}
		else{
			ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER, this,null);
		}

	}
			
	/**
	 * The PIM Data associated with this PIM Action.
	 * Every action has a Data object.
	 */
	private PIMData dataAction;
	
	
	/**
	 * Prepare the data associated with this action.
	 * 
	 * @param nodeData
	 * @param dataAssociation
	 */
	protected void prepareActionData(Node nodeData, Node dataAssociation){
		String sName=nodeData.getAttributes().getNamedItem("name").getNodeValue();			
		String sPSMContainersXPath=nodeData.getAttributes().getNamedItem("storedin").getNodeValue();		
		dataAction=new PIMData(sName,sPSMContainersXPath);
		if(dataAssociation!=null){
			String sAssociationType=dataAssociation.getAttributes().getNamedItem("assoType").getNodeValue();
			dataAction.setDataAssociation(new PIMDataAssociation(sAssociationType));
		}
	}
	
	/**
	 * 
	 * @return Returns the data associated with this action.
	 */
	public PIMData getActionData(){
		return dataAction;
	}
	
	/**
	 * 
	 * @return Returns a list of applicable transformers of this PIM Action
	 */
	private ArrayList<PSMTransformer> applicableTransformers(){
		ArrayList<PSMTransformer> alFilteredTransformers=new ArrayList<PSMTransformer>();
		//The PSM Containers associated with the data of this action
		SMContainer []arrDataContainers=dataAction.getPSMContainers();
		//The transformers associated with this action
		PSMTransformer[] arrTransformers=preparePSMTransformers();
		
		//No more transformers mean we don't have to
		//refine further
		if(arrTransformers.length>0){
			
			for(int i=0; i<arrDataContainers.length; ++i){			
				String sDataContainer=arrDataContainers[i].getContainerXPath();
				
				for(int j=0; j<arrTransformers.length; ++j){
					String sTransformerInput=arrTransformers[j].getInputContainerXPath();
					String[] sArrInputContainer=sTransformerInput.split(" ");
					boolean bBreak=false;
					
					for(int k=0; k<sArrInputContainer.length; ++k){
						if(sDataContainer.equalsIgnoreCase(sArrInputContainer[k])){					
							alFilteredTransformers.add(arrTransformers[j]);								
							bBreak=true;
							break;
						}
					}				
					
					
					if(bBreak) break;
					}									
				}
		}
				
		//update count of applicable transformers
		this.iNumApplicableCrossTransformers=alFilteredTransformers.size();
		
		return alFilteredTransformers;
	}
	
	
	/**
	 * Refinement call on PIMAction
	 */
	public void tau(){
		
		if(!this.getHasInnerTransformers()){
			//System.out.println("PIM tau()");
			if(sRefinementType.equalsIgnoreCase("setRefmnt"))
				crossSetRefinement();		
			else if(sRefinementType.equalsIgnoreCase("seqRefmnt"))
				crossSeqRefinement();
		}else {
			if(sRefinementType.equalsIgnoreCase("setRefmnt"))
				innerSetRefinement();
			else if(sRefinementType.equalsIgnoreCase("seqRefmnt"))
				innerSeqRefinement();
		}
	}

	
	private void innerSetRefinement() {
		//Get our applicable PIM transformers and call tau() on each one
		PIMAction[] arrTransformers = preparePIMActions();
		//update count of applicable transformers
		this.iNumApplicableCrossTransformers=arrTransformers.length;
		
		if(arrTransformers.length>1){
			
			for(int i=0; i<arrTransformers.length; ++i){	
				
				if(i<arrTransformers.length-1){
					ActionRefinement.createXmlResult(Reason.SET_PROCESSING, null,null);
				}
				arrTransformers[i].oParentEvent=this;
				arrTransformers[i].iOutputSequence=i;
				arrTransformers[i].tau();							
			}
			ActionRefinement.createXmlResult(Reason.SET_END_PROCESSING, null, arrTransformers.length-1);
		}
		else if(arrTransformers.length==1){
			arrTransformers[0].oParentEvent=this;
			arrTransformers[0].tau();
		}
		else{
			ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER, this, null);
		}
	}
		
	private void innerSeqRefinement() {
		//Get our applicable PIM transformers and call tau() on each one
		PIMAction[] arrTransformers = preparePIMActions();
		//update number of applicable inner transformers
		this.iNumApplicableInnerTransformers=arrTransformers.length;
		
		//very necessary for sequence
		Arrays.sort(arrTransformers);
		
		if(arrTransformers.length>1){
			//arrange output
			ActionRefinement.createXmlResult(Reason.SEQUENCE_START, null,null);
			//looping
			for(int i=arrTransformers.length-1; i>=0; --i){
				arrTransformers[i].oParentEvent=this;
				arrTransformers[i].iOutputSequence=i;
				arrTransformers[i].tau();			
				ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER_IN_SEQ, arrTransformers[i], null );
			}			
			ActionRefinement.createXmlResult(Reason.SEQUENCE_END, null,arrTransformers.length);							
		}
		else if(arrTransformers.length==1){
			arrTransformers[0].oParentEvent=this;
			arrTransformers[0].tau();
		}
		else{
			ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER, this,null);		
		}
	}
	
}
