import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.tum.in.i22.pdp.cm.FastServiceHandler;
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
		FastServiceHandler pdpServer = new FastServiceHandler(50001);
		
		Thread thread = new Thread(pdpServer);
		thread.start();
		
		try {
			_logger.debug("Pause the main thread for 1s (PDP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
		
		pdp = new Pep2PdpFastImp("localhost", 50001);
	}
	
	@Test
	public void test() throws Exception{
		// create event
		IMessageFactory mf = MessageFactory.getInstance();
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);
		
		// connect to pdp
		pdp.connect();
		// notify event1
		EStatus status1 = pdp.notifyEvent(event1);
		_logger.debug("Received status as reply to event 1: " + status1);
		
		EStatus status2 = pdp.notifyEvent(event2);
		_logger.debug("Received status as reply to event 2: " + status2);
		// disconnect from pdp
		pdp.disconnect();
		
		try {
			_logger.debug("Pause the main thread for 1s (PDP stopping).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
		
		// check if status is not null
		Assert.assertNotNull(status1);
		Assert.assertNotNull(status2);
	}
	
	
	private  Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

}
