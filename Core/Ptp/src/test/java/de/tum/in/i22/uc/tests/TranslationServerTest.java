package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.transport.TTransport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.ptp.PtpHandler;

public class TranslationServerTest {

	private static TTransport transport;
	/**
	 * The tests are by default disabled.
	 * The tests assume that a translation server is running.
	 * When testing manually, one has to start by hand the server.
	 */
	private static final boolean TESTS_ENABLED = false;

	private static PtpHandler ptpHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}

		ptpHandler = new PtpHandler();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
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
		XmlPolicy xmlPolicy = new XmlPolicy(requestId, policy);
		xmlPolicy.setTemplateId(parameters.get("template_id"));
		xmlPolicy.setTemplateXml(policy);
		String translatedPolicy = "";
		translatedPolicy = ptpHandler.translatePolicy(requestId, parameters, xmlPolicy).getPolicy().getXml();
		//translatedPolicy = client.translatePolicy(requestId, parameters, policy);
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
