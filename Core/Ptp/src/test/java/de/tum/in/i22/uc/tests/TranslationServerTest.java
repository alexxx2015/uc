package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
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

import de.tum.in.i22.uc.remotelistener.TranslationEngine;
import de.tum.in.i22.uc.remotelistener.TranslationEngine_ThriftServer;
import de.tum.in.i22.uc.remotelistener.TranslationException;

public class TranslationServerTest {

	private static TTransport transport;
	private static TranslationEngine.Client client;
	private static final boolean TESTS_ENABLED = false;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		} 
		try {
			 
			//this is an old example of the ptp server
			//the new version uses TAny2PmpThriftServer server
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
		if(!TESTS_ENABLED){
			return;
		}
		transport.close();
	}
	
	@Test
	public void translatePolicySimpleTest() {
		if(!TESTS_ENABLED){
			assertTrue("TranslationServerTest disabled", true);
			return;
		}
		String file = "src/test/resources/policies4test/" +"102_no_copy.xml";
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
		if(!TESTS_ENABLED){
			assertTrue("TranslationServerTest disabled", true);
			return;
		}
		String file = "src/test/resources/policies4test/" + "102_no_copy.xml";
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
		if(!TESTS_ENABLED){
			assertTrue("TranslationServerTest disabled", true);
			return;
		}
		String file = "src/test/resources/policies4test/" + "102_no_copy.xml";
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
		System.out.println(translatedPolicy);
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
