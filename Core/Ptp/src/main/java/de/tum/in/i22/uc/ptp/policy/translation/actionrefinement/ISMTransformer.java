package de.tum.in.i22.uc.ptp.policy.translation.actionrefinement;

import java.util.ArrayList;
import java.util.Arrays;

import org.w3c.dom.Node;

/**
 * 14 April 2013
 * ISM Transformer
 * 
 */
public class ISMTransformer extends Event {
	
	/**
	 * Creates an instance of ISM Transformer
	 * 
	 * @param name
	 * @param seqNumber
	 * @param refinementType
	 * @param parent
	 */
	public ISMTransformer(String name, int seqNumber, String refinementType, Object parent){
		super(name,seqNumber,"ISM Transformer",refinementType);
		oParent=parent;
	}
	
	/**
	 * The input container xpath address of this transformer
	 */
	private String sInputContainerXPath;
	
	/**
	 * Sets the xpath address of the input containers of this Transformer
	 * 
	 * @param containerXPath
	 */
	public void setInputContainerXPath(String containerXPath){
		this.sInputContainerXPath=containerXPath;		
	}
	
	/**
	 * 
	 * @return Returns the xpath address of input containers of this transformer
	 */
	public String getInputContainerXPath(){			
		return sInputContainerXPath;
	}
	
	/**
	 * 
	 * @return Returns an array of input containers of this ISM Transformers
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
	 * Sets the parent container of this transformer.
	 * 
	 * @param parent
	 */
	public void setParentContainer(SMContainer[] parent){
		oParentContainer=parent;
	}
	
	/**
	 * 
	 * @return Returns the parent container of this transformer.
	 */
	public SMContainer[] getParentContainer(){
		return oParentContainer;
	}
	
	/**
	 * The output container xpath address. Useful for
	 * creating instances of output containers.
	 */
	private String sOutputContainerXPath;
	
	/**
	 * Sets the output container xpath address
	 * 
	 * @param containerXPath
	 */
	public void setOutputContainerXPath(String containerXPath){
		this.sOutputContainerXPath=containerXPath;
	}
	
	/**
	 * 
	 * @return Returns the output container xpath address
	 */
	public String getOutputContainerXPath(){
		return sOutputContainerXPath;
	}
	
	/**
	 * Array of output containers of this transformer
	 */
	private SMContainer[] arrOutputContainer;
	
	/**
	 * White-space separated xpath addresses to
	 * child PSM or ISM transformers
	 */
	private String sTransformersXPath;
	
	
	/**
	 * Build a list of all PSM or ISM transformers of this PSM Transformer
	 * 
	 * @return Returns a list of child transformers of this transformers
	 */
	public ISMTransformer[] prepareTransformers(){
			ISMTransformer[] arrISMTransformer=new ISMTransformer[0];			
			
			//1st step:
			if(sTransformersXPath!=null && !sTransformersXPath.equals("") ){
				
				String[] sArrTransformer=sTransformersXPath.split(" ");
				arrISMTransformer=new ISMTransformer[sArrTransformer.length];
				//System.out.println("ISM Transformer path: "+sTransformersXPath);
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
					//try to get cross transformers
					String sTransformers="";					
					Node nodeCrossTransformers=nodeTransformer.getAttributes().getNamedItem("ismRefmnt");
					if(nodeCrossTransformers!=null) 
						sTransformers=nodeCrossTransformers.getNodeValue();				
					
					//4th step: transformers can be inner PSM transformers or
					//cross ISM transformers					
					
						ISMTransformer transformer=new ISMTransformer(sName,iSeq,sRefinementType,this);
						transformer.setInputContainerXPath(sInputContainerXPath);
						transformer.setOutputContainerXPath(sOutputContainerXPath);
						transformer.setTransformersXPath(sTransformers);						
						//we trickle the parent container down for transformer exclusion
						//transformer.setParentContainer(arrInputContainer);					
						//oTransformers.add(transformer);
						arrISMTransformer[i]=transformer;						
						/*
						ISMTransformer transformer=new ISMTransformer(sName,iSeq,sRefinementType, this);
						transformer.setInputContainerXPath(sInputContainerXPath);
						transformer.setOutputContainerXPath(sOutputContainerXPath);
						transformer.setTransformers(sTransformers);						
						//we trickle the parent container down for transformer exclusion
						//transformer.setParentContainer(arrInputContainer);					
						//oTransformers.add(transformer);
						events[i]=transformer;*/
					
						
				}
			}
		
		return arrISMTransformer;
	}			
	
	/**
	 * 
	 * @return Returns a array list of applicable transformers of this ISM Transformer
	 */
	private ArrayList<ISMTransformer> applicableISMTransformers(){
		ArrayList<ISMTransformer> alFilteredTransformers=new ArrayList<ISMTransformer>();
		//The PSM Containers associated with the data of this action
		SMContainer []arrDataContainers=prepareInputContainer();
		//The transformers associated with this action
		ISMTransformer[] arrTransformers=prepareTransformers();
		
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
							//This is helpful to properly close <and> and <or> tags in
							//the resulting xml refined output, just in case the ISM
							//transformers are the last in their level
							alFilteredTransformers.add(arrTransformers[j]);
							bBreak=true;
							break;
						}
					}									
					
					if(bBreak) break;
				}									
			}
		}
		
		//update number of applicable inner transformers
		this.iNumApplicableInnerTransformers=alFilteredTransformers.size();
		
		return alFilteredTransformers;
	}
	
	/**
	 * Performs inner set refinement
	 */
	private void innerSetRefinement(){		
		//Get our applicable ISM transformers and call tau() on each one
//		Filtering the transformers!	
//		ArrayList<ISMTransformer> alTrans=applicableISMTransformers();
//		ISMTransformer[] arrTransformers=new ISMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new ISMTransformer[alTrans.size()]);
		
		ISMTransformer[] arrTransformers = prepareTransformers();
		//update number of applicable inner transformers
		this.iNumApplicableInnerTransformers=arrTransformers.length;
		
		if(arrTransformers.length>1){				//have more than 1 child
			
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
		else if(arrTransformers.length==1){			//have just a child
			arrTransformers[0].oParentEvent=this;
			arrTransformers[0].tau();
		}
		else{										//have no child @ all
			ActionRefinement.createXmlResult(Reason.A_FINAL_TRANSFORMER, this,null);
		}
	}	
		
	
	/**
	 * Returns an ordered array of PSM transformers
	 */
	private void innerSeqRefinement(){
		//Get our applicable ISM transformers and call tau() on each one
//		Filtering the transformers!	
//		ArrayList<ISMTransformer> alTrans=applicableISMTransformers();
//		ISMTransformer[] arrTransformers=new ISMTransformer[alTrans.size()];
//		arrTransformers=alTrans.toArray(new ISMTransformer[alTrans.size()]);
		
		ISMTransformer[] arrTransformers = prepareTransformers();
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
	public void setTransformersXPath(String transformersXPath){
		this.sTransformersXPath=transformersXPath;
	}
		
	/**
	 * Our refinement routine. It recursively refines within the
	 * ISM layer if necessary 
	 */
	public void tau(){		
//		System.out.println("ISM Pie()");
		if(sRefinementType.equalsIgnoreCase("setRefmnt"))
			innerSetRefinement();		
		else if(sRefinementType.equalsIgnoreCase("seqRefmnt"))
			innerSeqRefinement();	
	}
	
	//------------
	private Object oParent;
	public Object getParent(){
		return oParent;
	}
	//------------
	
	
}
