package de.tum.in.i22.uc.policy.translation.actionrefinement;


/**
 * 13 April 2013
 * Event is parent of PIMAction, PSMTransformer and ISMTransformer
 * 
 */
public abstract class Event implements Comparable<Object>{	
	
	/**
	 * Creates an instance of an event.
	 */
	public Event(){
		//
		iOutputSequence=0;
	}
	
	/**
	 * Creates an instance of an event.
	 * 
	 * @param name
	 * @param seqNumber
	 * @param transformerType
	 * @param refinementType
	 */
	public Event(String name, int seqNumber, String transformerType, String refinementType){
		this.sName=name;
		this.iSequenceNumber=seqNumber;
		this.sTransformerType=transformerType;
		this.sRefinementType=refinementType;
		//Everybody has output sequence of zero initially
		//This might change during refinement
		iOutputSequence=0;
	}
		
	/**
	 * The name of this action or transformer
	 */
	protected String sName;
	
	/**
	 * 
	 * @return Returns the name of this event.
	 */
	public String getName() {
		return sName;
	}
	
	/**
	 * The sequence number telling the position in
	 * an ordered sequence of transformers or actions   
	 */
	protected int iSequenceNumber;
	/**
	 * 
	 * @return Returns the sequence number of this
	 *         event in its parent list of child transformers
	 */
	public int getSequenceNumber() {
		return iSequenceNumber;
	}
	

	/**
	 * Determines if this transformer is an action in PIM layer
	 * or its a transformer in PSM or ISM layer 
	 */
	protected String sTransformerType;
	/**
	 * 
	 * @return Return the type of transformer e.g. ISM or PSM
	 */
	public String getTransformerType() {
		return sTransformerType;
	} 
	
		
	/**
	 * Determines if the actions under this base transformer
	 * are mutually exclusive or they occur in an ordered sequence
	 */
	protected String sRefinementType;
	/**
	 * 
	 * @return Return the refinement type
	 */
	public String getRefinementType(){
		return sRefinementType;
	}				
	
	//What the user is expecting
	protected String sOutputFile;
	
	/**
	 * Sets the output file name of result.
	 * 
	 * @param output
	 */
	public void setOutputFile(String output){
		sOutputFile=output;
	}
	
	
	/**
	 * Path to our domain model (e.g. sns.xml) 
	 */
	protected String sDomainModelFile;
	
	/**
	 * Set the domain file
	 * 
	 * @param domainFile
	 */
	public void setDomainModelFile(String domainFile){
		sDomainModelFile=domainFile;
	}
	
	/**
	 * Every event has a parent event. Note that the parent of
	 * a PIMAction event is null however.
	 */
	protected Event oParentEvent;
	
	/**
	 * Helpful in sorting transformers
	 * 
	 * @param event
	 */
	@Override
	public int compareTo(Object event) {
		
		Event e=(Event) event;
		if(this.iSequenceNumber<e.iSequenceNumber) return -1;
		else if(this.iSequenceNumber>e.iSequenceNumber) return 1;
		else return 0;
	}
	
	/**
	 * Number of applicable cross transformers of an Event.
	 * This is very useful for back-tracking across our domain sns file
	 * to parent in a higher model level (e.g. PSM to PIM) and
	 * helpful in determining the nature of action refinement 
	 * output. 
	 */
	protected int iNumApplicableCrossTransformers;
	
	/**
	 * Number of applicable inner transformers of an Event.
	 * This is very useful for back-tracking within a domain 
	 * model level (e.g. PSM or ISM)
	 * in determining the nature of action refinement 
	 * output. 
	 */
	protected int iNumApplicableInnerTransformers;
	
	
	/**
	 * A boolean flag indicating the presence of inner transformers
	 */
	private boolean bHasInnerTransformers;	
	
	/**
	 * 
	 * @return Returns a boolean status if this PSM Transformer has inner transformers
	 */
	public boolean getHasInnerTransformers(){
		return bHasInnerTransformers;
	}
	
	/**
	 * Set has inner transformers
	 * 
	 * @param hasInnerTransformers
	 */
	public void setHasInnerTransformers(boolean hasInnerTransformers){
		bHasInnerTransformers=hasInnerTransformers;
	}
	
	/**
	 * Our refinement routine. It recursively refines within each layer if necessary
	 */
	public abstract void tau();
	
	/**
	 * During refinement especially sequence refinement, we will 
	 * need to output nodes in reverse fashion. Therefore,
	 * this helps us identify the sequence of this event
	 */
	protected int iOutputSequence;
	
	
}
