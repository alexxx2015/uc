package de.tum.in.i22.uc.policy.translation.actionrefinement;

/**
 * 14 April 2013
 * PIMData belongs to a  PIMAction.
 * 
 * @author ELIJAH
 *
 */
public class PIMData {
	/**
	 * Creates an instance of a PIMData
	 * 
	 * @param name
	 * @param childContainersXPath
	 */
	public PIMData(String name, String childContainersXPath){
		sName=name;
		sChildContainersXPath=childContainersXPath;	
		preparePSMContainers();
	}
	
	private String sName;
	/**
	 * 
	 * @return Returns the name of this PIMData
	 */
	public String getName(){
		return sName;
	}
	
	private String sChildContainersXPath;
	/**
	 * 
	 * @return Returns the child containers xpath string.
	 */
	public String getChildContainersXPath(){
		return sChildContainersXPath;
	}
	
	//The set of child PSM containers associated with
	//this PIM Data
	private SMContainer[] arrPSMContainers;
	
	/**
	 * Will be called from outside when necessary
	 * to make runtime efficient
	 */	
	public void preparePSMContainers(){
		if(!sChildContainersXPath.equals("") || sChildContainersXPath!=null){
			//step 1:
			String[] sArrContainers=sChildContainersXPath.split(" ");
			arrPSMContainers=new SMContainer[sArrContainers.length];
			for(int i=0; i<arrPSMContainers.length; ++i){
				//1st level PSM Containers have null parents
				arrPSMContainers[i]=new SMContainer(sArrContainers[i],null);				
			}
		}
		else{
			
		}
	}
	
	/**
	 * 
	 * @return Returns the set of PSM Containers
	 */
	public SMContainer[] getPSMContainers(){
		return arrPSMContainers;
	}
		
	private PIMDataAssociation dataAssociation;
	
	/**
	 * 
	 * @return Returns the data PIMDataAssociation of this PIMData
	 */
	public PIMDataAssociation getDataAssociation(){
		return dataAssociation;
	}
	
	/**
	 * Sets the PIMDataAssociation of this data
	 * 
	 * @param association
	 */
	public void setDataAssociation(PIMDataAssociation association){
		dataAssociation=association;
	}
}
