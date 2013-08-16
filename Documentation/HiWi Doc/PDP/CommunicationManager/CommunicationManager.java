package CommunicationManager;
import PDPManager.IPDP;

/**
 * Manages Incoming and Outgoing connections
 * (e.g. TCP-Server, WebServer, Java JNI)
 * Maintains references
 * @author Tobias
 * @version 1.0
 * @created 16-Mai-2013 14:49:38
 */
public class CommunicationManager implements IUCRegistry2PDP, ICommunication {

	private int PIPConnection;
	private int PMPConnection;
	private int UCRegistryConnection;
	public CommunicationHandler m_CommunicationHandler;

	public CommunicationManager(){

	}

	public void finalize() throws Throwable {

	}
	public void getStatus(){

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
	 * @param container
	 */
	public Data[0..*] getDataInContainer(Container container){
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
	 * @param executeActions
	 */
	public Status execute(Event[0..*] executeActions){
		return null;
	}
}//end CommunicationManager