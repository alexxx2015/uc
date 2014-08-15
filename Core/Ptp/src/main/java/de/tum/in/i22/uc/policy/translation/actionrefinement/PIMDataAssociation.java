package de.tum.in.i22.uc.policy.translation.actionrefinement;

/**
 * 21 April 2013
 * PIMData has @ most 1 PIMDataAssociation.
 * 
 */
public class PIMDataAssociation {
	
	/**
	 * Creates an instance of PIMDataAssociation
	 * 
	 * @param associationType
	 */
	public PIMDataAssociation(String associationType){
		sAssociationType=associationType;
	}
	
	private String sAssociationType;
	/**
	 * 
	 * @return Returns the association type of this PIMDataAssociation.
	 */
	public String getAssociationType(){
		return sAssociationType;
	}
	
	/**
	 * Sets the association type of this PIMDataAssociation
	 * 
	 * @param associationType
	 */
	public void setAssociationType(String associationType){
		sAssociationType=associationType;
	}
	
	private String sTargetAssociationXPath;
	
	/**
	 * Sets the target association xpath of this PIMDataAssociation
	 * 
	 * @param path
	 */
	public void setTargetAssociationXPath(String path){
		sTargetAssociationXPath=path;
	}
}
