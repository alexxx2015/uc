package de.tum.in.i22.uc.ptp.oldhandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/oldhandler/TranslationEngine_TcpServer.java
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/oldhandler/TranslationEngine_TcpServer.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.utilities.Config;
=======
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.utilities.PtpLogger;
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/remotelistener/TranslationEngine_TcpServer.java
=======
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.utilities.PtpLogger;
>>>>>>> remotes/origin/dev-fk:Core/Ptp/src/main/java/de/tum/in/i22/uc/remotelistener/TranslationEngine_TcpServer.java

/**
 * @author Cipri
 * This class is the main class used to start the PTP (Translation Engine).
 */
public class TranslationEngine_TcpServer {
	
	private ServerSocket serverSocket;
	private PtpLogger logger; 	
	
	public static final int TE_Default_PORT = 50001 ;
	private static int TE_PORT = 0;
	
	public TranslationEngine_TcpServer(){
		logger = PtpLogger.translationLoggerInstance();
	}
	
	public static int getPort(){
		if(TE_PORT == 0){
			try {
				Config config = new Config();
				String portString=config.getProperty("translationEnginePort");
				int port = Integer.parseInt(portString);
				TE_PORT = port;
			} catch (Exception e) {
				return TE_Default_PORT;
			}	
			
		}
		return TE_PORT;
	}
	
	public static void main(String[] args) {

		int port = getPort() ;
		if(args.length != 1){
			System.out.println("PTP Running on default port: " + port);			
		}
		else{
			try{
				String portString = args[0];
				port = Integer.parseInt(portString);
			} catch (Exception ex){
				ex.printStackTrace();
				System.out.println("Running on default port: " + port);
			}						
		}
										
		TranslationEngine_TcpServer listener = new TranslationEngine_TcpServer();
		listener.startListening(port);
		
	}
		
	
	public void startListening(int port){
		
		
		 try {
			serverSocket = new ServerSocket(port);
			String msg = "TranslationListener listening at port: " + port;
			logger.infoLog(msg, null);
			while(true){
	            try {
	            	Socket client = serverSocket.accept();                
	                Translator connection = new Translator(client);
	                new Thread(connection).start();
	            } catch (IOException e) {
	            	String message = "error accepting request";
	            	e.printStackTrace();
	            	logger.errorLog(message, e);
	            }
	        }
			
		} catch (IOException e) {
			String message = "error starting server port";
        	e.printStackTrace();
        	logger.errorLog(message, e);
		}
	}
	
	
		

}
