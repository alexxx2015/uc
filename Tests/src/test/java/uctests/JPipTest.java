package uctests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class JPipTest extends GenericTest {

	private static Logger _logger = LoggerFactory.getLogger(JPipTest.class);

	@Test
	public void testJPip() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		
		int port=55666;
		String id="Mylistener";
		
		IThriftServer srv= ThriftServerFactory.createJPipThriftServer(port);
		new Thread(srv).start();
		
		pip.addListener("localhost", port, id, " ");
		
		pip.setUpdateFrequency(2000, id);
		
		
		Map<String, String> map;
		IEvent event;
		
		map = new HashMap<>();
		map.put("PID", "4567");
		map.put("PEP", "java");
		map.put("delimiter", "end");
		map.put("id", "Source23");
		map.put("fileDescriptor", "123");
		event = new EventBasic("Source", map);
		
		pip.update(event);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pip.update(event);
		pip.update(event);
		pip.update(event);
		pip.update(event);
		pip.update(event);
		pip.update(event);
			
		//if it arrives here without throwing exceptions, the test is passed.
		Assert.assertEquals(false, false);
	}
}
