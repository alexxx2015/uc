package helper;

import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

import structures.PDPEvent;

public class PIPTCPConnectionHandler implements Runnable {
	private IPIPCommunication pipLib;
	private Socket clientSocket;
	private boolean exit;

	private boolean debug = true;

	private Thread internalThread;

	// FIXME does it work this way? Never tested, and the tutorials are not very
	// helpful..
	// Where does Java save the log file? Any more configuration needed?
	// Need to flush? If so: How to flush?
	private static Logger log = Logger.getLogger("PIP TCP");

	public PIPTCPConnectionHandler(Socket clientSocket, IPIPCommunication pipLib) {
		this.pipLib = pipLib;
		this.clientSocket = clientSocket;

		internalThread = new Thread(this);
		internalThread.start();
	}

	// / <summary>
	// / Waits for incoming function call requests to the external UC4Win
	// interface, parses and evaluates them, and returns with the answer of the
	// PDP/PIP/PEP.
	// / </summary>
	private void processRequests() {
		BufferedReader inputStream;
		PrintWriter outputStream;

		exit = false;
		try {
			outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
			inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while (!exit) {
				try {
					String incomingMessage = inputStream.readLine();
					
					if (incomingMessage == null)
						return;

					log.log(
							Level.INFO,
							"PIP: Received data from client["
									+ clientSocket.getLocalAddress()
									+ clientSocket.getLocalPort() + "] = "
									+ incomingMessage);

					String[] splittedMessage = incomingMessage.split(",");

					String functionName = splittedMessage[0];

					switch (functionName) {
					case "initialRepresentation":
						if (splittedMessage.length != 3) {
							outputStream.println("-1");
							break;
						}

						int initialData = pipLib.initialRepresentation(
								Integer.parseInt(splittedMessage[1]),
								splittedMessage[2]);
						outputStream.println(initialData);

//						log.log(Level.INFO,
//								"initialRepresentation request received - answered with ["
//										+ initialData + "]");
//
//						System.out
//								.println("initialRepresentation request received - answered with ["
//										+ initialData + "]");

						break;
						
					case "updatePIP":
						if (splittedMessage.length < 2) {
							outputStream.println("-1");
							break;
						}
						
						@SuppressWarnings("rawtypes")
						List<Hashtable> parameters = new ArrayList<Hashtable>();
						
						String actionName = splittedMessage[1];
						
						try {
							for(int i = 2; i < splittedMessage.length; i++){
								String[] paramValue = splittedMessage[i].split(":");
								if(paramValue.length == 2){
									String[] paramValue1 = paramValue[0].split("=");
									String[] paramValue2 = paramValue[1].split("=");
									
									if((paramValue1.length == 2) && (paramValue2.length == 2)){
										Hashtable<String,String> newHashtable = new Hashtable<String,String>();
										newHashtable.put(paramValue1[0], paramValue1[1]);
										newHashtable.put(paramValue2[0], paramValue2[1]);										
										parameters.add(newHashtable);
									}
								}
							}	
						} catch (Exception e) {
							outputStream.println(e.toString());
							outputStream.println("An error occured - invalid arguments provided");
//							log.log(Level.WARNING, "An error occured - invalid arguments provided");
//							System.out.println("An error occured - invalid arguments provided");
							break;
						}
						
						PDPEvent newEvent = new PDPEvent();
						newEvent.action = actionName;
						newEvent.parameters = parameters;

						int returnValue = 1;//pipLib.updatePIP(newEvent);
						outputStream.println(returnValue);

//						log.log(Level.INFO,
//								"updatePIP request received - answered with ["
//										+ returnValue + "]");

//						System.out.println("updatePIP request received - answered with ["+ returnValue + "]");

						break;

					case "representationRefinesData":
						if (splittedMessage.length != 5) {
							outputStream.println("-1");
							break;
						}

						// FIXME: Code was: boolean.Parse(splittedMessage[4]);
						// how does the Message look? Does this work?
						// boolean param4 = (splittedMessage[4] == "0") ? false
						// : true;

						int status = pipLib.representationRefinesData(
								Integer.parseInt(splittedMessage[1]),
								splittedMessage[2],
								Integer.parseInt(splittedMessage[3]),
								(splittedMessage[4] == "0") ? false : true);
						outputStream.println(status);

//						log.log(Level.INFO,
//								"representationRefinesData request received - answered with ["
//										+ status + "]");
//
//						System.out
//								.println("representationRefinesData request received - answered with ["
//										+ status + "]");

						break;

					case "printModel":
						String model = pipLib.printModel();
						outputStream.println("Information Flow Model:\n");

						String[] modelArray = model.split("\n");

						for (String m : modelArray) {
							outputStream.println(m);
						}

//						log.log(Level.INFO,
//								"printModel request received - answered with: ["
//										+ model + "]");
//
//						System.out
//								.println("printModel request received - answered with: ["
//										+ model + "]");

						break;
						
					case "help":
						outputStream.println("PIPInterface:");
						outputStream.println("invokation style [functionName],[param1],[param2],[param3],...,[paramn]\n");
						outputStream.println("Available functions:");
						outputStream.println("[int initialRepresentation(int PID, string rep)]");
						outputStream.println("[int updatePIP(string actionName, string param1Name, string param1Value, string param2Name, string param2Value, ...)]");
						outputStream.println("[int representationRefinesData(int PID, string rep, int dataID, bool strict)]");
						outputStream.println("[string printModel]");
						outputStream.println("[void exit]\n");
						outputStream.println("---");
						break;

					case "exit":
						clientSocket.close();
						exit = true;

						break;

					default:
						outputStream
								.println("An error occured - invalid arguments provided");

//						log.log(Level.WARNING,
//								"An error occured - invalid arguments provided");
//
//						System.out
//								.println("An error occured - invalid arguments provided");
						break;
					}

					outputStream.flush();
				} catch (SocketException se) {
					se.printStackTrace();
//					log.log(Level.INFO, "Connection to client lost");
//					System.out.println("Connection to client lost");
					break;
				} catch (Exception e) {

//					log.log(Level.SEVERE,
//							"An exception occured - type:" + e.getClass()
//									+ " = " + e.getStackTrace());

//					System.out.println("An exception occured - type: e.getClass() + " = " + e.getStackTrace());
					break;
				}
			}
		}
		catch (Exception e) {
//			log.log(
//					Level.SEVERE,
//					"An exception occured - type:" + e.getClass() + " = "
//							+ e.getStackTrace());

//			System.out.println("An exception occured - type:" + e.getClass() = " + e.getStackTrace());
		}
	}

	// / <summary>
	// / Kill all internal threads to free resources.
	// / </summary>
	public void abortInternalThread() {
		exit = true;
		internalThread = null;
	}

	@Override
	public void run() {
		processRequests();
	}
}
