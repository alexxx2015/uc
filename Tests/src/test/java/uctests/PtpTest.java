package uctests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class PtpTest {

	private static final boolean TESTS_ENABLED = false;
	private static Logger _logger = LoggerFactory.getLogger(PtpTest.class);
	private static Pmp2PmpClient clientPmp;

	private static final String ptpProjectLocation = ".."+File.separator+"Core"+File.separator+"Ptp";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}


		Settings.getInstance().loadSetting(Settings.PROP_NAME_ptpProjectLocation, ptpProjectLocation);

		/*
		 * Start PMP server.
		 * Start PDP.
		 * Use the Controller in the CommunicationManager to start a real pdp.
		 */
		int PORT = 21001;
//		PmpHandler pmpProcessor = new PmpHandler();
//		PipProcessor pip = new MyDummyPipProcessor();
//		PdpProcessor pdp = new DummyPdpProcessor();
//		pmpProcessor.init(pip, pdp);
//		IThriftServer pmpServer = ThriftServerFactory.createPmpThriftServer(PORT, pmpProcessor);
//		new Thread(pmpServer).start();

		/*
		 * Connect to the PMP server
		 */
		try{
			String HOST = "127.0.0.1";
			ThriftClientFactory thriftClientFactory = new ThriftClientFactory();
			clientPmp=thriftClientFactory.createPmp2PmpClient(new IPLocation(HOST, PORT));
			clientPmp.connect();
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
			System.out.println("Please make sure that the PDP, PMP is running - start Controller!");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void translatePolicySimpleTest() {
		if(!TESTS_ENABLED){
			assertTrue("TranslationServerTest disabled", true);
			return;
		}
		String file = ptpProjectLocation +File.separator + "src"+File.separator+"test"+File.separator+"resources"+File.separator+"policies4test"+File.separator +"102_no_copy.xml";
		String policy = loadTestPolicy(file);

		Map<String,String> parameters = new HashMap<String,String>();
		//parameters.put("template_id", "102");
		parameters.put("object_instance", "test_object");

		IPtpResponse translatedPolicy = translatePolicy(1, parameters, policy);

		String logMsg = "Translation result: " + translatedPolicy.getStatus().toString() + " "+ translatedPolicy.getPolicy();
		_logger.debug(logMsg);
		assertTrue(translatedPolicy!=null);
	}

	@Test
	public void translateAndDeployPolicySimpleTest() {
		if(!TESTS_ENABLED){
			assertTrue("TranslationServerTest disabled", true);
			return;
		}
		String file = "src/test/resources/policies4test/" +"102_no_copy.xml";
		String policy = loadTestPolicy(file);

		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("template_id", "102");
		parameters.put("object_instance", "test_object");

		IPtpResponse translatedPolicy = translatePolicy(1, parameters, policy);

		String logMsg = "Translation result: " + translatedPolicy.getStatus().toString() + " "+ translatedPolicy.getPolicy();
		_logger.debug(logMsg);
		assertTrue(translatedPolicy!=null);

		XmlPolicy xmlPolicy = translatedPolicy.getPolicy();
		IStatus deployStatus = clientPmp.deployPolicyXMLPmp(xmlPolicy);
		logMsg = "Deployment Status: " + deployStatus.toString();
		_logger.debug(logMsg);

		assertTrue(deployStatus!=null);

	}

	private static IPtpResponse translatePolicy(int id, Map<String,String> parameters, String policy){
		String requestId = "test_"+id+"_translate_policy";
		XmlPolicy xmlPolicy = new XmlPolicy("policy_before_translation", policy);
		String templateId = parameters.get("template_id");
		if(templateId != null)
			xmlPolicy.setTemplateId(templateId);

		String logMsg = requestId +"\n" + policy;
		_logger.debug(logMsg);
		IPtpResponse response = clientPmp.translatePolicy(requestId, parameters, xmlPolicy);

		logMsg = response.getStatus().toString() + " " + response.getPolicy().toString();
		_logger.debug(logMsg);
		return response;
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


