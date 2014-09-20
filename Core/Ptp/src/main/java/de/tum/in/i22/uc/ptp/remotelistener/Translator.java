package de.tum.in.i22.uc.ptp.remotelistener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.policy.translation.TranslationController;
import de.tum.in.i22.uc.ptp.policy.translation.Filter.FilterStatus;

/**
 * @author Cipri
 *
 */
public class Translator implements Runnable {

	private Socket clientSocket ;
	private static final Logger logger = LoggerFactory.getLogger(Translator.class);
	private OutputStream output;
	private InputStream input;
	
	public Translator(Socket client) {
		this.clientSocket = client;
	}

	@Override
	public void run() {
		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
		} catch (IOException e){
			e.printStackTrace();
			logger.error("exception", e);
			try {
				clientSocket.close();
			} catch (IOException e2) {
				e2.printStackTrace();
				logger.error("exception", e2);
			}
			return;
		}
		System.out.println("\n\n---------------------------------------------");
		System.out.println("NEW CLIENT:" + new Date());
		boolean loop = true ;
		while(loop){
			try{
				
				//receive
				byte[] data = this.receive();
				if(data == null)
					continue;
				Message msg = new Message(data);
				String logMsg = "received "+ (new Date()) +" :" + msg.toString();
				System.out.println(logMsg);
				logger.info(logMsg);
				
				String header = msg.header();
				if(header.equals(TranslationProtocol.POLICY_IN)){				
					msg = translatePolicy(msg);
				}
				else if(header.equals(TranslationProtocol.TRANSLATION_END)){
					//end loop
					break;
				}
				else {
					msg = new Message(TranslationProtocol.INCORRECT_INPUT, TranslationProtocol.INCORRECT_INPUT, TranslationProtocol.INCORRECT_INPUT);
				}
				
				//send response
				String msgText = msg.toString();
				logMsg = "sent: " + msgText;
				send(msgText.getBytes());
				logger.info(logMsg);
				System.out.println(logMsg);		
										
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("exception", e);
				
				try {
					clientSocket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
					logger.error("exception", e2);
				}
				break;			
			}
		}
		
		//String inputStr = readPolicyFile();
	}
	
	public Message translatePolicy(Message msg){
		String inputPolicy = msg.payload();
		
		String[] params = msg.param().split("<#>");
		
		String message = "Start translating policy: " + inputPolicy;
		logger.info(message);
		
		String outputPolicy = params[0]+"_"+params[1]+"_"+"policytranslated.xml";
		Map<String,String> params2 = new HashMap<String,String>();
		params2.put("templated_id", params[0]);
		params2.put("object_instance", params[1]);
		
		TranslationController translationController ;
		FilterStatus status ;
		try{
			translationController = new TranslationController(inputPolicy, params2);
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();
			message = "Translation successful: " + translationController.getMessage();
		} catch (Exception ex){
			status = FilterStatus.FAILURE;
			System.out.println(ex.getLocalizedMessage());
			logger.error("translation exception", ex);
		}		
		
		//System.out.println(outputPolicy);
		if(status == FilterStatus.SUCCESS){			
			logger.info(message);
			msg = new Message(TranslationProtocol.RESULT_OK, msg.param(), outputPolicy);	
		}
		else{
			message = "Translation failed: " + status.name();
			logger.error(message);
			msg = new Message(TranslationProtocol.RESULT_ERROR, msg.param(), "NULL");
		}
		
		return msg;
	}
	
	private static String readPolicyFile(String file){
		String policy = "";
			String message = "Loading config file: "+ file;
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line = bufferedReader.readLine();
											
				policy = line;
				bufferedReader.close();
			} catch (Exception e) {
				String erroMsg = "ERROR reading configuration file!";
				e.printStackTrace();
			}
	 
			System.out.println(policy);
			return policy;
					
		}
	
	
	
	/**
	 * Sends bytes to the connected host
	 * 
	 * @param message
	 *            a set of bytes containing the message in ASCII format
	 * @throws IOException
	 */
	private void send(byte[] message) throws IOException {
		if (message.length > 0) {
			byte[] messageWrapper = new byte[message.length + 1];
			//remove CR
			//System.arraycopy(message, 0, messageWrapper, 0, message.length);
			//messageWrapper[messageWrapper.length - 1] = 13;
			messageWrapper = message ;
			output.write(messageWrapper);
			output.flush();
		}
	}

	/**
	 * Tries to receive a message from the server
	 * 
	 * @return a set of bytes representing the message in ASCII
	 * @throws IOException
	 */
	private byte[] receive() throws IOException {
		byte[] messageBytes = new byte[128 * 1024];
		int numberOfBytesRead = input.read(messageBytes);
		if (numberOfBytesRead > 0) {
			byte[] ret = new byte[numberOfBytesRead];
			System.arraycopy(messageBytes, 0, ret, 0, ret.length);
			return ret;
		}
		return null;
	}

	public static void main(String[] args){
		String file = "d:\\Users\\Cipri_L\\GitHub_cipri88\\PTP\\policy_ptp\\policyinput.xml";
		String inputFile = readPolicyFile(file);
		Map<String,String> params2 = new HashMap<String,String>();
		params2.put("templated_id", "107");
		params2.put("object_instance", "object");
		
		String outputPolicy = "test-output.xml";
		TranslationController translationController ;
		FilterStatus status ;
		try{
			translationController = new TranslationController(inputFile, params2);
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();
		} catch (Exception ex){
			status = FilterStatus.FAILURE;
			System.out.println(ex.getLocalizedMessage());
		}	
		
	}
	
}
