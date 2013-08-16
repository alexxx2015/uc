package CommunicationManager;

/**
 * @author Dominik
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public interface ICommunication {

	/**
	 * 
	 * @param predicate
	 */
	public boolean evaluatePredicate(string predicate);

	/**
	 * 
	 * @param executeActions
	 */
	public Status execute(Event[0..*] executeActions);

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