package pdp.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;

public class TestParent {
	protected File settingsFile = new File("uc.properties");

	protected void exitOnException(Exception e) {
		e.printStackTrace();
		destroySettings();
		System.exit(1);
	}

	@After
	@Before
	public void destroySettings() {
		settingsFile.delete();
	}

	protected void createSettings(Map<String,String> settings) {
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

}