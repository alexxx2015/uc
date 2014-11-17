package uctests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

public class DistrTest {
	private static Logger _logger = LoggerFactory.getLogger(DistrTest.class);

	private static IAny2Pdp _pdp = null;
	private static IAny2Pmp _pmp = null;

	private static Map<String,String> _params = new HashMap<>();

	private File settingsFile = new File("uc.properties");

	private static Map<String,String> params = new HashMap<>();

	@After
	public void destroySettings() {
		settingsFile.delete();
	}


	private void sleep(long millis) {
		if (millis <= 0) {
			return;
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			new RuntimeException(e.getMessage());
		}
	}

	private void exitOnException(Exception e) {
		e.printStackTrace();
		destroySettings();
		System.exit(1);
	}

	private void createSettings(Map<String,String> settings) {
		if (settingsFile.exists()) {
			throw new IllegalStateException("uc.properties file must not exist for this test to be executed.");
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(settingsFile));
		} catch (IOException e) {
			exitOnException(e);
		}

		for (Entry<String, String> s : settings.entrySet()) {
			try {
				writer.write(s.getKey() + "=" + s.getValue());
				writer.newLine();
			} catch (IOException e) {
				exitOnException(e);
			}
		}

		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			exitOnException(e);
		}
	}


	@SuppressWarnings("serial")
	@Test
	public void test() {

		createSettings(new HashMap<String,String>() {{
			put("distributionEnabled","true");
		}});

		Controller box = new Controller();
		box.start();

		_pdp = box;
		_pmp = box;

		/*
		 * This policy inhibits the event (action,{(name,value)}),
		 * if it happened at least two times in the last 1000 milliseconds.
		 * Evaluation is performed every 500 milliseconds.
		 */
		_pmp.deployPolicyURIPmp("src/test/resources/testPolicies/testOccurMinEvent.xml");

		params.clear();
		params.put("prog", "ls");

		_pdp.notifyEventSync(new EventBasic("*", params, false));


	}

}
