package de.tum.in.i22.uc.ptp.policy.translation.actionrefinement;

import java.util.ArrayList;
import org.w3c.dom.Node;

/**
 * 14 April 2013
 * Base Container represents data in PIM layer and data containers
 * in PSM and ISM layers.
 * 
 * @author ELIJAH
 * 
 */
public class SMContainer {		
	
	/**
	 * Used from PSMTransformers and ISMTransformers.
	 * 
	 * @param containerXPath
	 * @param parentContainer
	 */
	public SMContainer(String containerXPath, Object parentContainer){		
		sContainerXPath=containerXPath;		
		oParentContainer=parentContainer;
		prepareFurther();
	}
	
	/**
	 * Populates the fields of this object.
	 */
	private void prepareFurther(){
		//refine xpath address
		String sExpression=ActionRefinement.getNewXPathAddress(sContainerXPath);
		//Get node
		Node nodeContainer=ActionRefinement.processXpathExpression(sExpression).item(0);
		String sName=nodeContainer.getAttributes().getNamedItem("name").getNodeValue();
		setName(sName);
		Node nodeChildContainers=null;
		nodeChildContainers=nodeContainer.getAttributes().getNamedItem("contimplementedas");
		sChildContainersXPath="";
		if(nodeChildContainers!=null)
		sChildContainersXPath=nodeChildContainers.getNodeValue();
	}
	
	private String sContainerXPath;
	/**
	 * 
	 * @return Returns the container xpath of this container.
	 */
	public String getContainerXPath(){
		return sContainerXPath;
	}
	
	//The name of a container: e.g copy or copy_cmd
	private String sName;
	/**
	 * 
	 * @return Returns the name of this container. 
	 */
	public String getName(){
		return sName;
	}
	
	/**
	 * Sets the name of this container.
	 * 
	 * @param name
	 */
	public void setName(String name){
		sName=name;
	}
	
	//A white-space separated list of xpath expressions
	//used to determine the child containers.
	private String sChildContainersXPath;
	/**
	 * 
	 * @return Returns the child container xpath of this container.
	 */
	public String getChildContainersXPath(){
		return sChildContainersXPath;
	}
	
	private SMContainer[] arrChildContainers;
	
	/**
	 * Prepares the child containers of this container
	 */
	private void prepareContainers(){
		if(!sChildContainersXPath.equals("") || sChildContainersXPath!=null){
			//step 1:
			if(sChildContainersXPath.contains(" ")){
				String[] sArrContainers=sChildContainersXPath.split(" ");
				arrChildContainers=new SMContainer[sArrContainers.length];
				for(int i=0; i<arrChildContainers.length; ++i){
					//1st level PSM Containers have null parents
					arrChildContainers[i]=new SMContainer(sArrContainers[i],null);
				}
			}
			else{
				arrChildContainers=new SMContainer[1];
				arrChildContainers[0]=new SMContainer(sChildContainersXPath,null);
			}
		}
		else{
			
		}
	}
	
	/**
	 * 
	 * @return Returns the child containers of this container.
	 */
	public SMContainer[] getChildContainers(){
		return arrChildContainers;
	}
		
	
	//Helpful to recursively check for parents
	private Object oParentContainer;
	/**
	 * 
	 * @return Returns the parent container of this container.
	 */
	public Object getParentContainer(){
		return oParentContainer;
	}
	
	/**
	 * Sets the parent container of this container.
	 * 
	 * @param parent
	 */
	public void setParentContainer(Object parent){
		oParentContainer=parent;
	}
}
