package pdptests;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;
import de.tum.in.i22.pdp.cm.out.pip.IPdp2PipFast;
import de.tum.in.i22.pdp.cm.out.pip.Pdp2PipImp;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ContainerBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pip.PipController;

public class Pdp2PipTest {
	
	private static Logger _logger = Logger.getRootLogger();
	
	private static PipController _pipController;
	private static IPdp2PipFast _pipProxy;
	private final static int _pipPort = 60011;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pipController = new PipController();
		_pipController.start(_pipPort);
		try {
			_logger.debug("Pause the main thread for 1s (PIP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
		_pipProxy = new Pdp2PipImp("localhost", _pipPort);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// connect pdp to pip
		_pipProxy.connect();
	}

	@After
	public void tearDown() throws Exception {
		_pipProxy.disconnect();
	}
	
	@Test
	public void testEvaluatePredicate() {
		IEvent event = new EventBasic("event1", null);
		String predicate = "dummy string";
		boolean res = _pipProxy.evaluatePredicate(event, predicate);
		_logger.debug("Received result: " + res);
		Assert.assertEquals(true, res);
	}
	
	@Test
	public void testGetDataInContainer() {
		IContainer container = new ContainerBasic("class value", null);
		List<IData> list = _pipProxy.getDataInContainer(container);
		_logger.debug("Received result: " + Arrays.toString(list.toArray()));
		
		Assert.assertNotNull(list);
	}
	
	@Test
	public void testGetContainerForData() {
		IData data = new DataBasic(UUID.randomUUID().toString());
		List<IContainer> list = _pipProxy.getContainerForData(data);
		_logger.debug("Received result: " + Arrays.toString(list.toArray()));
		
		Assert.assertNotNull(list);
	}
	
	@Test
	public void testNotifyActualEvent() {
		IEvent event = DummyMessageGen.createEvent();
		IResponse response = _pipProxy.notifyActualEvent(event);
		_logger.debug("Received response: " + response);
		Assert.assertNotNull(response);
	}
	
	
	@Test
	public void testMultipleInvocations() {
		for (int i = 0; i < 10; i++) {
			double num = Math.random();
			if (num <= 0.33) {
				testGetContainerForData();
			} else if (num <= 0.66) {
				testEvaluatePredicate();
			} else {
				testGetDataInContainer();
			}
		}
	}
	

}
