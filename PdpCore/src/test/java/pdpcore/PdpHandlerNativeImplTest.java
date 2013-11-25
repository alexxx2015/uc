package pdpcore;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fraunhofer.iese.pef.pdp.LinuxPolicyDecisionPoint;
import de.tum.in.i22.pdp.core.PdpHandlerPdpNative;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

public class PdpHandlerNativeImplTest {
	
	private static PdpHandlerPdpNative _pdpHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdpHandler = new PdpHandlerPdpNative(new LinuxPolicyDecisionPoint());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		
		
		Map<String, String> map = new HashMap<>();
		map.put("val1", "value1");
		map.put("val2", "value2");
		IResponse response = _pdpHandler.notifyEvent(new EventBasic("testEvent", map, false));
		
		if (response == null) {
			Assert.fail("Expected response different than null.");
		}
	}

}
