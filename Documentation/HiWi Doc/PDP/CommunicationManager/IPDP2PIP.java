package CommunicationManager;

/**
 * @author Tobias
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public interface IPDP2PIP {

	/**
	 * 
	 * @param predicate
	 */
	public boolean evaluatePredicate(string predicate);

	/**
	 * 
	 * @param data
	 */
	public Container[0..*] getContainerForData(Data data);

	/**
	 * 
	 * @param container
	 */
	public Data[0..*] getDataInContainer(Container container);

}