package uctests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.cm.out.pip.IPdp2PipFast;
import de.tum.in.i22.pdp.cm.out.pip.Pdp2PipImp;
import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.PipSettings;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipTest {
	
	private static Logger _logger = Logger.getRootLogger();
	
	private static PipController _pipController;
	private static IPdp2PipFast _pipProxy;
	private final static int PDP_LISTENER_PORT = 60011;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Injector injector = Guice.createInjector(new PdpTestModule());
		_pipController =  injector.getInstance(PipController.class);
		PipSettings pipSettings = _pipController.getPipSettings();
		pipSettings.setPdpListenerPortNum(PDP_LISTENER_PORT);
		_pipController.start();
		try {
			_logger.debug("Pause the main thread for 1s (PIP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
		_pipProxy = new Pdp2PipImp("localhost", PDP_LISTENER_PORT);
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
		Map<String, String> map = new HashMap<>();
		IEvent event = new EventBasic("event1", map);
		String predicate = "dummy string";
		boolean res = _pipProxy.evaluatePredicateSimulatingNextState(event, predicate);
		_logger.debug("Received result: " + res);
		Assert.assertEquals(true, res);
	}
	
	@Test
	public void testGetDataInContainer() {
//		IContainer container = new ContainerBasic("class value", null);
//		//List<IData> list = _pipProxy.getDataInContainer(container);
//		_logger.debug("Received result: " + Arrays.toString(list.toArray()));
//		
//		Assert.assertNotNull(list);
	}
	
	@Test
	public void testGetContainerForData() {
//		IData data = new DataBasic(UUID.randomUUID().toString());
//		//FIXME: 
//		//List<IContainer> list = _pipProxy.getContainerForData(data);
//		_logger.debug("Received result: " + Arrays.toString(list.toArray()));
//		
//		Assert.assertNotNull(list);
	}
	
	@Test
	public void testNotifyActualEvent() {
		IEvent event = DummyMessageGen.createEvent();
		IStatus status = _pipProxy.notifyActualEvent(event);
		_logger.debug("Received status: " + status);
		Assert.assertNotNull(status);
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
