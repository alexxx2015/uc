package uctests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;

public class PmpTest {
	private static Logger log = LoggerFactory.getLogger(PmpTest.class);

	@Test
	public void test() {

		RequestHandler box = new RequestHandler(new LocalLocation(), new LocalLocation(), new LocalLocation());
		IAny2Pmp pmp = box;
		
		
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
		
		pmp.receivePolicies(Collections.singleton(policyString));
		
		box.notifyEventSync(new EventBasic("testEvent", Collections.<String, String> emptyMap()));
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (true);
	}

}
