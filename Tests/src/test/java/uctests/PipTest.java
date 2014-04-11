package uctests;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class PipTest {

	private static Logger _logger = LoggerFactory.getLogger(PipTest.class);

	private static IAny2Pdp _pdp;
	private static IAny2Pip _pip;
	private static IAny2Pmp _pmp;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdp = new PdpHandler();
		_pip = new PipHandler();
		_pmp = new PmpHandler();
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
	public void testEvaluatePredicate() {
		Map<String, String> map = new HashMap<>();
		IEvent event = new EventBasic("event1", map);
		String predicate = "dummy string";
		boolean res = _pip.evaluatePredicateSimulatingNextState(event, predicate);
		_logger.debug("Received result: " + res);
		Assert.assertEquals(false, res);
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
		IEvent event = new EventBasic("x", Collections.EMPTY_MAP);
		IStatus status = _pip.update(event);
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
