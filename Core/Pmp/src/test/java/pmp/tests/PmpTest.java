package pmp.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.PmpHandler;

public class PmpTest {
	private static Logger log = LoggerFactory.getLogger(PmpTest.class);

	@Test
	public void test() {
		PmpHandler pmp=new PmpHandler();
		String path="src/test/resources/testPmp.xml";

		byte[] encoded=null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// To be safe, encoding should be added to the next command
		String policyString=new String(encoded);
		
		String iter=pmp.convertPolicies(Collections.singleton(policyString));
		String iter2=pmp.convertPolicies(Collections.singleton(iter));
		
		
		assert (true);
	}

}
