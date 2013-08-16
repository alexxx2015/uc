package CommunicationManager;

/**
 * @author Dominik
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public class CommunicationHandler implements IIncoming {

	public InputParser m_InputParser;

	public CommunicationHandler(){

	}

	public void finalize() throws Throwable {

	}
	/**
	 * 
	 * @param predicate
	 */
	public boolean evaluatePredicate(string predicate){
		return false;
	}

	/**
	 * 
	 * @param event
	 */
	public Status execute(Event[0..*] event){
		return null;
	}

	/**
	 * 
	 * @param data
	 */
	public Container[0..*] getContainerForData(Data data){
		return null;
	}

	/**
	 * 
	 * @param container
	 */
	public data : Data[0..*] getDataInContainer(Container container){
		return null;
	}
}//end CommunicationHandler