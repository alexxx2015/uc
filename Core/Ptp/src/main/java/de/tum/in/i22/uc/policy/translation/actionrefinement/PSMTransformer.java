package de.tum.in.i22.uc.policy.translation.actionrefinement;

import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.Node;

/**
 * 14 April 2013
 * PSM Transformer
 * 
 */
public class PSMTransformer extends Event {
	
	/**
	 * Creates an instance of a PSM Transformer
	 * 
	 * @param name
	 * @param seqNumber
	 * @param refinementType
	 * @param parent
	 */
	public PSMTransformer(String name, int seqNumber, String refinementType, Object parent){
		super(name,seqNumber,"PSM Transformer",refinementType);
		oParent=parent;
	}
	
	/**
	 * The input container xpath address of this transformer
	 */
	private String sInputContainerXPath;
	/**
	 * Sets the input container xpath address of this transformer
	 * 
	 * @param containerXPath
	 */
	public void setInputContainerXPath(String containerXPath){
		this.sInputContainerXPath=containerXPath;		
	}
	/**
	 * 
	 * @return Returns the xpath address of input containers
	 */
	public String getInputContainerXPath(){			
		return sInputContainerXPath;
	}
	
	/**
	 * 
	 * @return Returns an array of input containers of this transformer
	 */
	private SMContainer[] prepareInputContainer(){
		SMContainer[] arrInputContainer=null;
		String[] sArr=sInputContainerXPath.split(" ");
		arrInputContainer=new SMContainer[sArr.length];
		for(int i=0; i<sArr.length; ++i){
			arrInputContainer[i]=new SMContainer(sArr[i],null);	
		}
		return arrInputContainer;
	}
	
	//To help us with exclusion of transformers
	private SMContainer[] oParentContainer;
	
	/**
	 * Sets the parent container of this PSM Transformer.
	 * 
	 * @param parent
	 */
	public void setParentContainer(SMContainer[] parent){
		oParentContainer=parent;
	}
	
	/**
	 * 
	 * @return Returns the parent container of this PSM Transformer.
	 */
	public SMContainer[] getParentContainer(){
		return oParentContainer;
	}
	
	/**
	 * Output container xpath of this transformer
	 */
	private String sOutputContainerXPath;
	
	/**
	 * Set the output container xpath address of this transformer
	 * 
	 * @param containerXPath
	 */
	public void setOutputContainerXPath(String containerXPath){
		this.sOutputContainerXPath=containerXPath;
	}
	
	/**
	 * 
	 * @return Returns the output container xpath of this transformer
	 */
	public String getOutputContainerXPath(){
		return sOutputContainerXPath;
	}
	
	/**
	 * An array of output containers of this transformer
	 */
	private SMContainer[] arrOutputContainer;
	
	/**
	 * White-space separated xpath addresses to
	 * child PSM or ISM transformers
	 */
	private String sTransformersXPath;
	
	/**
	 * 
	 * @return Build an array of all PSM transformers of this PSM Transformer
	 */
	public PSMTransformer[] preparePSMTransformers(){
			PSMTransformer[] arrPSMTransformer=null;			
			
			//1st step:
			if(sTransformersXPath!=null && !sTransformersXPath.equals("") ){
				
				String[] sArrTransformer=sTransformersXPath.split(" ");
				arrPSMTransformer=new PSMTransformer[sArrTransformer.length];
				
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
					
					//prepare PSM Transformer
					PSMTransformer transformer=new PSMTransformer(sName,iSeq,sRefinementType, this);
					transformer.setInputContainerXPath(sInputContainerXPath);
					transformer.setOutputContainerXPath(sOutputContainerXPath);
					transformer.setTransformers(sTransformers);
					transformer.setHasInnerTransformers(bHasInTransformers);
					//we trickle the parent container down for transformer exclusion
					//transformer.setParentContainer(arrInputContainer);					
					//oTransformers.add(transformer);
					arrPSMTransformer[i]=transformer;							
						
				}
			}
		
		return arrPSMTransformer;
	}
	
	
	/**
	 * 
	 * @return Returns an array of ISM Transformers of this Transformer
	 */
	public ISMTransformer[] prepareISMTransformers(){
			ISMTransformer[] arrISMTransformer=new ISMTransformer[0];			
				
			//1st step:
			if(sTransformersXPath!=null && !sTransformersXPath.equals("") ){
					
				String[] sArrTransformer=sTransformersXPath.split(" ");
				arrISMTransformer=new ISMTransformer[sArrTransformer.length];
					
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
					Node nodeInputContainer=nodeTransformer.getAttributes().getNamedItem("inputimplecontainer");
					if(nodeInputContainer!=null) sInputContainerXPath=nodeInputContainer.getNodeValue();
					String sOutputContainerXPath="";
					Node nodeOutputContainer=nodeTransformer.getAttributes().getNamedItem("outputimplecontainer");
					if(nodeOutputContainer!=null) sOutputContainerXPath=nodeOutputContainer.getNodeValue();					
					//set inner ism transformers path
					String sTransformers="";																
					Node nodeCrossTransformers=nodeTransformer.getAttributes().getNamedItem("ismRefmnt");
					if(nodeCrossTransformers!=null) {
						sTransformers=nodeCrossTransformers.getNodeValue();								
					}							
										
					ISMTransformer transformer=new ISMTransformer(sName,iSeq,sRefinementType, this);
					transformer.setInputContainerXPath(sInputContainerXPath);
					transformer.setOutputContainerXPath(sOutputContainerXPath);
					transformer.setTransformersXPath(sTransformers);						
					//we trickle the parent container down for transformer exclusion
					//transformer.setParentContainer(arrInputContainer);					
					//oTransformers.add(transformer);
					arrISMTransformer[i]=transformer;												
				}
			}
			
			return arrISMTransformer;
		}
	
	/**
	 * 
	 * @return Returns an array list of applicable PSM Transformers
	 */
	private ArrayList<PSMTransformer> applicablePSMTransformers(){
		ArrayList<PSMTransformer> alFilteredTransformers=new ArrayList<PSMTransformer>();
		//The PSM Containers associated with the data of this action
		SMContainer []arrDataContainers=prepareInputContainer();
		//The transformers associated with this action
		PSMTransformer[] arrTransformers=preparePSMTransformers();
		
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
		
		//update number of applicable inner transformers
		this.iNumApplicableInnerTransformers=alFilteredTransformers.size();
		
		return alFilteredTransformers;
	}
	
	/**
	 * 
	 * @return Returns an array list of applicable ISM Transformers
	 */
	private ArrayList<ISMTransformer> applicableISMTransformers(){
		ArrayList<ISMTransformer> alFilteredTransformers=new ArrayList<ISMTransformer>();
		//The PSM Containers associated with the data of this action
		SMContainer []arrDataContainers=prepareInputContainer();
		//The transformers associated with this action
		ISMTransformer[] arrTransformers=prepareISMTransformers();		
		
		
		for(int i=0; i<arrDataContainers.length; ++i){			
			//String sDataContainer=arrDataContainers[i].getContainerXPath();
			//We need get all ISM Containers associated with this PSM Container
			String sDataContainer=arrDataContainers[i].getContainerXPath();			
			SMContainer psmContainer=new SMContainer(sDataContainer,null);
			String [] arrISMContainer=psmContainer.getChildContainersXPath().split(" ");			
			//help escape out of inner loops
			boolean bBreak=false;
			
			for(int l=0; l<arrISMContainer.length; ++l){
			
				for(int j=0; j<arrTransformers.length; ++j){
					String sTransformerInput=arrTransformers[j].getInputContainerXPath();
					String[] sArrInputContainer=sTransformerInput.split(" ");
					
					
					for(int k=0; k<sArrInputContainer.length; ++k){
						
						if(arrISMContainer[l].equalsIgnoreCase(sArrInputContainer[k])){	
							arrTransformers[j].oParentEvent=this;
							//This is helpful to properly close <and> and <or> tags in
							//the resulting xml refined output, just in case the ISM
							//transformers are the last in their level
							alFilteredTransformers.add(arrTransformers[j]);								
							bBreak=true;
							break;
						}
					}									
					
				
				}
										
			}
		}
		
		//update number of applicable ISM transformers		
		this.iNumApplicableCrossTransformers=alFilteredTransformers.size();
		
		return alFilteredTransformers;
	}
	
	/**
	 * Perform cross set refinement
	 */
	private void crossSetRefinement(){		
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!	
//		ArrayList<ISMTransformer> alTrans=applicableISMTransformers();
//		ISMTransformer[] arrTransformers=new ISMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new ISMTransformer[alTrans.size()]);
				
		ISMTransformer[] arrTransformers = prepareISMTransformers();		
		//update number of applicable ISM transformers		
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
			ActionRefinement.createXmlResult(Reason.SET_END_PROCESSING, null,arrTransformers.length-1);					
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
	 * Perform cross sequence refinement
	 */
	private void crossSeqRefinement(){				
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!	
//		ArrayList<ISMTransformer> alTrans=applicableISMTransformers();
//		ISMTransformer[] arrTransformers=new ISMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new ISMTransformer[alTrans.size()]);
		
		ISMTransformer[] arrTransformers = prepareISMTransformers();
		this.iNumApplicableCrossTransformers=arrTransformers.length;
		
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
	
	/**
	 * Perform inner set refinement
	 */
	private void innerSetRefinement(){
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!		
//		ArrayList<PSMTransformer> alTrans=applicablePSMTransformers();
//		PSMTransformer[] arrTransformers=new PSMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new PSMTransformer[alTrans.size()]);
			
		PSMTransformer[] arrTransformers = preparePSMTransformers();
		//update number of applicable inner transformers
		this.iNumApplicableInnerTransformers=arrTransformers.length;
				
		if(arrTransformers.length>1){			
			for(int i=0; i<arrTransformers.length; ++i){	
				if(i<arrTransformers.length-1){
					ActionRefinement.createXmlResult(Reason.SET_PROCESSING, null,null);
				}
				arrTransformers[i].oParentEvent=this;
				arrTransformers[i].iOutputSequence=i;
				arrTransformers[i].tau();
			}
			ActionRefinement.createXmlResult(Reason.SET_END_PROCESSING, null,arrTransformers.length-1);					
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
	 * Perform inner sequence refinement
	 */
	private void innerSeqRefinement(){
		//Get our applicable PSM transformers and call tau() on each one
//		Filtering the transformers!
//		ArrayList<PSMTransformer> alTrans=applicablePSMTransformers();
//		PSMTransformer[] arrTransformers=new PSMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new PSMTransformer[alTrans.size()]);
		
		PSMTransformer[] arrTransformers = preparePSMTransformers();
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
	
	
	/**
	 * The xpath provided here can be for cross PSM transformers
	 * or for within PSM transformers
	 * 
	 * @param transformersXPath
	 */
	public void setTransformers(String transformersXPath){
		this.sTransformersXPath=transformersXPath;
	}
	
	
	/**
	 * The recursive refinement routine.
	 */
	public void tau(){
//		System.out.println("PSM Tau()");
		
		if(!this.getHasInnerTransformers()){
			
			if(sRefinementType.equalsIgnoreCase("setRefmnt"))
				crossSetRefinement();
			else if(sRefinementType.equalsIgnoreCase("seqRefmnt"))
				crossSeqRefinement();
		}					
		else {
			if(sRefinementType.equalsIgnoreCase("setRefmnt"))
				innerSetRefinement();
			else if(sRefinementType.equalsIgnoreCase("seqRefmnt"))
				innerSeqRefinement();
		}
	}
	
	//---------------------
	private Object oParent;
	public Object getParent(){
		return oParent;
	}
	//-------------------
	
	
}
