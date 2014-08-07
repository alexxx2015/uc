package de.tum.in.i22.uc.policy.classes;

/**
 * 18 March 2013
 * 
 * ResourceCreator has the logic for creating concrete instances
 * of containers from arguments passed to our policy editor. It
 * processes the arguments and then returns the concrete instance.
 * 
 * @author ELIJAH
 *
 */
public class ResourceCreator {
	/**
	 * A resource entity
	 */
	private Resource entity;
	/**
	 * Creates an instance of a resource creator for use.
	 */
	private ResourceCreator(){
		entity=new Resource();
	}
	/**
	 * Responsible for creating a concrete resource. The array arguments
	 * are from the command-line prepared and passed in to the editor from
	 * our Firefox extension.
	 * 
	 * @param args
	 * @return Returns a concrete resource instance.
	 */
	public static Resource create(String[] args){
		
		ResourceCreator creator=new ResourceCreator();
		
		//Attempt to create a concrete instance of an entity
		creator.createContainer(args[0]);
		
		if(creator.getContainer()==null) return null;
		
		//set the complete path to this entity on a file system
		creator.setDataSource(args[1]);
		
		//set the description
		creator.setDescription(args[2]);
		
		//further processing...
		if(args.length>3) creator.setExtraInformation(args[3]);
				
		return creator.getContainer();		
	}
	
	/**
	 * Creates a resource given data class information
	 * 
	 * @param sClass
	 * @return A concrete resource instance
	 */
	public static Resource create(String sClass){
		Resource resource;

		resource=new Resource();
		resource.sClass = sClass;
		resource.entityType = Resource.EntityType.SINGLETON; 
		
		return resource;
	}
	
	/**
	 * Removes unnecessary single or double quotes from parameters.
	 * This issue arises from our firefox extension.  
	 * 
	 * @param param
	 * @return A refined string without redundant quotes
	 */
	private String filter(String param){
		String result=param;
		if(param.startsWith("'") || param.endsWith("'")) 			
			result=param.replaceAll("\'", "").trim();
		if(param.startsWith("\"") || param.endsWith("\""))
			result=param.replaceAll("\"", "").trim();			
		return result;
	}
	
	/**
	 * Creates a container from command-line argument information.
	 * The present implementation covers images and collection of images.
	 * This is a point of extension for future additions to support other
	 * file (container) types.
	 * 
	 * @param arg
	 */
	private void createContainer(String arg) {
		String s1stArg=filter(arg);
		if(s1stArg.equalsIgnoreCase("-img")) entity=new Resource();
		else if(s1stArg.equalsIgnoreCase("-imgalbum")) entity=new Resource();
		else entity=null;
	}
	
	/**
	 * Sets the data source of this resource.
	 * 
	 * @param arg
	 */
	private void setDataSource(String arg){
		String s2ndArg=filter(arg);
		//split b4 assignment		
		String[] sArResult=s2ndArg.split("--");		
		entity.setDataSource(sArResult[1]);
	}
	
	/**
	 * Sets the description of this resource.
	 * 
	 * @param arg
	 */
	private void setDescription(String arg){
		String s3rdArg=filter(arg);
		//split b4 assignment		
		String[] sArResult=s3rdArg.split("--");		
		entity.setDescription(sArResult[1]);
	}
	
	/**
	 * Sets extra information of this resource
	 * 
	 * @param arg
	 */
	private void setExtraInformation(String arg){
		String s4thArg=filter(arg);
		//split b4 assignment		
		String[] sArResult=s4thArg.split("--");		
		entity.setExtraInformation(sArResult[1]);
	}		
	
	/**
	 * 
	 * @return Returns a prepared resource.
	 */
	public Resource getContainer(){
		return entity;
	}
}
