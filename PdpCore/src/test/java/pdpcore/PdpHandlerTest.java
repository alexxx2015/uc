package pdpcore;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pdp.core.PdpHandler;
import de.tum.in.i22.uc.cm.basic.EventBasic;

public class PdpHandlerTest {
	
	private static PdpHandler _pdpHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdpHandler = PdpHandler.getInstance();
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
		_pdpHandler.notifyEvent(new EventBasic("test", null, true));
	}

}
