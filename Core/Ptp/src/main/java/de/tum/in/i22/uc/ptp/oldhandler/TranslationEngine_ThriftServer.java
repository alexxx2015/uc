package de.tum.in.i22.uc.ptp.oldhandler;

import java.util.Date;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/oldhandler/TranslationEngine_ThriftServer.java
import de.tum.in.i22.uc.ptp.policy.translation.TranslationController;
import de.tum.in.i22.uc.ptp.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.ptp.utilities.Config;
=======
import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.policy.translation.Filter.FilterStatus;
import de.tum.in.i22.uc.policy.translation.TranslationController;
import de.tum.in.i22.uc.utilities.PtpLogger;
<<<<<<< HEAD:Core/Ptp/src/main/java/de/tum/in/i22/uc/ptp/oldhandler/TranslationEngine_ThriftServer.java
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264:Core/Ptp/src/main/java/de/tum/in/i22/uc/remotelistener/TranslationEngine_ThriftServer.java
=======
>>>>>>> remotes/origin/dev-fk:Core/Ptp/src/main/java/de/tum/in/i22/uc/remotelistener/TranslationEngine_ThriftServer.java

class TranslationEngineHandler implements TranslationEngine.Iface {

	private PtpLogger logger ;
	
	public TranslationEngineHandler(){
		this.logger = PtpLogger.translationLoggerInstance();
	}
	
	@Override
	public String translatePolicy(String requestId,	Map<String, String> parameters, String policy) throws TranslationException, TException {
		
		String timestamp = (new Date()).toString();
		String separator = "\n\n============="+ timestamp +"===============\n";
		String message = separator + "Req: "+requestId +"translateg policy: \n" + policy;
		System.out.println(message);
		logger.infoLog(message, null);
		
		String outputPolicy = parameters.get("template_id")+"_"+parameters.get("object_instance")+"_"+"policytranslated.xml";
		
		TranslationController translationController ;
		translationController = new TranslationController(policy, parameters);
		FilterStatus status ;
		try{
			translationController.filter();
			status = translationController.getFilterStatus();
			outputPolicy = translationController.getFinalOutput();
			message = "Translation successful: " + translationController.getMessage();
		} catch (Exception ex){
			status = FilterStatus.FAILURE;
			message = translationController.getMessage();
			System.out.println(message);
			logger.errorLog("translation exception", ex);
		}		
		
		if(status == FilterStatus.SUCCESS){			
			logger.infoLog(message, null);
		}
		else{
			message += "\nTranslation failed: " + status.name();
			logger.errorLog(message, null);
			throw new TranslationException(101, message);
		}
		
		return outputPolicy;
	}

	
}

public class TranslationEngine_ThriftServer {
	
	private static TranslationEngineHandler handler;
	private static TranslationEngine.Processor processor;

	public static final int TE_Default_PORT = 50001 ;
	private static int TE_PORT = 0;
	
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
	
	public static void startTranslationEngine(){
		
				
		try {
			 handler = new TranslationEngineHandler();
		     processor = new TranslationEngine.Processor(handler);

		      Runnable simple = new Runnable() {
		        public void run() {
		          simple(processor);
		        }
		      };      
		     
		      new Thread(simple).start();
		    } catch (Exception x) {
		      x.printStackTrace();
		    }
	}
	
	public static void main(String[] args) {
		
		TranslationEngine_ThriftServer.startTranslationEngine();
	}

	private static void simple(TranslationEngine.Processor processor) {
		try {
			int port = getPort();
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TSimpleServer(	new Args(serverTransport).processor(processor));
			System.out.println("Translation Engine Server started on port "
					+ port + " ...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
  }


}
