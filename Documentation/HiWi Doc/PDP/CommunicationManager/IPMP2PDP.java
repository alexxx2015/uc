package CommunicationManager;

/**
 * @author Tobias
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public interface IPMP2PDP {

	/**
	 * 
	 * @param mechanism
	 */
	public Status deployMechanism(Mechanism mechanism);

	/**
	 * 
	 * @param mechanismName
	 */
	public Status exportMechanism(string mechanismName);

	/**
	 * 
	 * @param mechanismName
	 */
	public Status revokeMechanism(string mechanismName);

}