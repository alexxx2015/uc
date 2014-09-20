package de.tum.in.i22.uc.ptp.remotelistener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.utilities.Config;

/**
 * @author Cipri
 * This class is the main class used to start the PTP (Translation Engine).
 */
public class TranslationEngine_TcpServer {
	
	private ServerSocket serverSocket;
	private static final Logger logger = LoggerFactory.getLogger(TranslationEngine_TcpServer.class);
	
	public static final int TE_Default_PORT = 50001 ;
	private static int TE_PORT = 0;
	
	public TranslationEngine_TcpServer(){
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
			logger.info(msg);
			while(true){
	            try {
	            	Socket client = serverSocket.accept();                
	                Translator connection = new Translator(client);
	                new Thread(connection).start();
	            } catch (IOException e) {
	            	String message = "error accepting request";
	            	e.printStackTrace();
	            	logger.error(message, e);
	            }
	        }
			
		} catch (IOException e) {
			String message = "error starting server port";
        	e.printStackTrace();
        	logger.error(message, e);
		}
	}
	
	
		

}
