package uctests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;

public class PmpTest extends GenericTest{
	private static Logger log = LoggerFactory.getLogger(PmpTest.class);

	@Test
	public void pmpTest() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

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

		pmp.deployPolicyRawXMLPmp(policyString);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pip.initialRepresentation(new NameBasic ("secondContainer"),pip.getDataInContainer(new NameBasic("initialContainer")));

		log.error("policy deployed. let's test it");

		Map<String,String> map = new HashMap<String,String>();
		map.put("name", "secondContainer");
		IResponse r =pdp.notifyEventSync(new EventBasic("testEvent", map));

		log.debug ("Response for secondcontainer : " + r);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertEquals(EStatus.INHIBIT, r.getAuthorizationAction().getEStatus());
		
	}

}
