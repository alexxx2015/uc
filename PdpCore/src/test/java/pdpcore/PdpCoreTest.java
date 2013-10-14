package pdpcore;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pdp.core.PdpHandler;

public class PdpCoreTest {
	
	private static PdpHandler _pdpHandler = null;
	
	@BeforeClass
	public static void beforeClass() {
		_pdpHandler = PdpHandler.getInstance();
	}

	@Test
	public void test() {
		fail("Not yet implemented");
		
		
	}

}
