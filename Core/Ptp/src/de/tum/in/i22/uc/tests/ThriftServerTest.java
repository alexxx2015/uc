package de.tum.in.i22.uc.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.policy.translation.Config;
import de.tum.in.i22.uc.remotelistener.TranslationEngine_ThriftServer;
import de.tum.in.i22.uc.remotelistener.thrift.TranslationEngine;
import de.tum.in.i22.uc.remotelistener.thrift.TranslationException;

public class ThriftServerTest {

	private static TTransport transport;
	private static TranslationEngine.Client client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 try {
			 
			 int port = TranslationEngine_ThriftServer.getPort();
			 			 
		      transport = new TSocket("localhost", port);
		      transport.open();

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      client = new TranslationEngine.Client(protocol);
		 } catch (TException x) {
		      x.printStackTrace();
		    }
		      
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		transport.close();
	}
	
	@Test
	public void translatePolicySimpleTest() {
		
		String file = System.getProperty("user.dir") + File.separator +"doc"+ File.separator +"policies4test" + File.separator +"102_no_copy.xml";
		String policy = loadTestPolicy(file);
		
		String translatedPolicy = "";
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("template_id", "102");
		parameters.put("object_instance", "test_object");
		translatedPolicy = translatePolicy(1, parameters, policy);
		
		assertTrue(translatedPolicy.length() > 0);
	}
	
	@Test
	public void translatePolicyEcaGUI() {
		
		String file = System.getProperty("user.dir") + File.separator +"doc"+ File.separator +"policies4test" + File.separator +"102_no_copy.xml";
		String policy = loadTestPolicy(file);
		
		String translatedPolicy = "";
		Map<String,String> parameters = new HashMap<String,String>();
		//parameters.put("template_id", "102");
		parameters.put("object_instance", "test_object");
		translatedPolicy = translatePolicy(1, parameters, policy);
		
		assertTrue(translatedPolicy.length() > 0);
	}
	
	@Test
	public void translatePolicyStressTest() {
		
		String file = System.getProperty("user.dir") + File.separator +"doc"+ File.separator +"policies4test" + File.separator +"102_no_copy.xml";
		String policy = loadTestPolicy(file);
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("template_id", "102");
		parameters.put("object_instance", "test_object");
		
		String translatedPolicy = "";
		for (int i=0; i<50; i++){
			translatedPolicy = "";
			translatedPolicy = translatePolicy(i, parameters, policy);
			if(translatedPolicy.length() == 0)
				break;
		}
		
		assertTrue(translatedPolicy.length() > 0);
	}

	private static String translatePolicy(int id, Map<String,String> parameters, String policy){
		String requestId = "test_"+id+"_translate_policy";
		
		String translatedPolicy = "";
		try {
			translatedPolicy = client.translatePolicy(requestId, parameters, policy);			
		}catch (TranslationException ex){
			System.out.println(ex.what +" "+ ex.why);
		}
		catch (TException e) {
			System.out.println("transport exception");
		}
		String timestamp = (new Date()).toString();
		System.out.println("===============================================");
		System.out.println(timestamp);
		System.out.println(requestId);
		return translatedPolicy;
	}
	
	private static String loadTestPolicy(String file){
		String policy = "";
			String message = "Loading test file: "+ file;
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line = bufferedReader.readLine();
											
				policy = line;
				bufferedReader.close();
			} catch (Exception e) {
				String erroMsg = "ERROR reading test file!";
				e.printStackTrace();
			}
	 
			System.out.println(policy);
			return policy;
					
		}
	
}
