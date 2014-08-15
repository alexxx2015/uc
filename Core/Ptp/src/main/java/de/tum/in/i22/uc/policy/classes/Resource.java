package de.tum.in.i22.uc.policy.classes;

/**
 * 18 March 2013.
 * This class names a file container which can be an image,
 * video or song. In fact, these mentioned ones are subclasses
 * of it. Sometimes, we become short of words to use to model
 * things.
 * 
 * @author ELIJAH
 *
 */
public class Resource {
	/**
	 * This represents the data class of this. Values are photo, song, video, e.t.c.
	 */
	protected String sClass;
	
	/**
	 * An entity can either be singleton (e.g. an image) or composite
	 * (e.g.) photo albums or collections of music or songs.
	 * 
	 * @author ELIJAH
	 *
	 */
	public enum EntityType{
		/**
		 * represents a single entity
		 */
		SINGLETON,	
		/**
		 * represents a collection of entities
		 */
		COMPOSITE	
	}
	
	protected EntityType entityType;
		
	
	/**
	 * Path to this file on a file system
	 */
	protected String sDataSource;
		
	/**
	 * Human meaningful description. For images, this will be its 
	 * title attribute and for albums this will be the album 
	 * name
	 */
	protected String sDescription;
	
	/**
	 * Some extra information needed to be persisted
	 */
	protected Object oExtraInformation;
	
	/**
	 * Extra information that can be used later by someone
	 */
	protected int iIdentifier;
	
	/**
	 * 
	 * @return Returns the data class of this resource e.g. photo or album 
	 */
	public String getDataClass(){
		return sClass;
	}
	/**
	 * 
	 * @return Returns the entity type i.e. singleton or composite
	 */
	public EntityType getType() {
		return entityType;
	}		
	
	/**
	 * 
	 * @return Returns the data source of this resource. The complete path
	 * 			to the resource
	 */
	public String getDataSource() {
		return sDataSource;
	}
	
	/**
	 * Set the data source
	 * 
	 * @param dataSource
	 */
	public void setDataSource(String dataSource){
		sDataSource=dataSource;
	}
	
	/**
	 * 
	 * @return Returns the resource description meaningful for users
	 */
	public String getDescription(){
		return sDescription;
	}
	
	/**
	 * Sets the description of this resource
	 * 
	 * @param description
	 */
	public void setDescription(String description){
		sDescription=description;
	}
	
	/**
	 * 
	 * @return Returns some extra information for this resource
	 */
	public Object getExtraInformation() {
		return oExtraInformation;
	}
	
	/**
	 * Sets some extra information for this resource
	 * 
	 * @param extraInformation
	 */
	public void setExtraInformation(Object extraInformation){
		oExtraInformation=extraInformation;
	}
}
