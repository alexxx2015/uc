import java.util.HashMap;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.tum.in.i22.pdp.cm.PdpServer;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;
import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;



public class TestPep2PdpCommunication {
	
	private static Logger _logger = Logger.getRootLogger();

	private static IPep2PdpFast pdp;
	
	static {
//		PdpServer pdpServer = new PdpServer(50001);
		
//		Thread thread = new Thread(pdpServer);
//		thread.start();
		
		pdp = new Pep2PdpFastImp("localhost", 50001);
	}
	
	@Test
	public void test() throws Exception{
		
		pdp.connect();
		
		IMessageFactory mf = MessageFactory.getInstance();
		//TODO add map as parameter
		IEvent event = mf.createEvent("event1", new HashMap<String, String>());
		
		EStatus status = pdp.notifyEvent(event);
		_logger.debug("Received status: " + status);
		
		pdp.disconnect();
		
		Assert.assertNotNull(status);
	}

}
