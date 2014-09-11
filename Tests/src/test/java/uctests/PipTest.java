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

public class PipTest extends GenericTest {

	private static Logger _logger = LoggerFactory.getLogger(PipTest.class);

	@Test
	public void testEvaluatePredicate() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		Map<String, String> map = new HashMap<>();
		IEvent event = new EventBasic("event1", map);
		String predicate = "dummy string";
		boolean res = pip.evaluatePredicateSimulatingNextState(event, predicate);
		_logger.debug("Received result: " + res);
		Assert.assertEquals(false, res);
	}

	@Test
	public void testGetDataInContainer() {
		// IContainer container = new ContainerBasic("class value", null);
		// //List<IData> list = _pipProxy.getDataInContainer(container);
		// _logger.debug("Received result: " + Arrays.toString(list.toArray()));
		//
		// Assert.assertNotNull(list);
	}

	@Test
	public void testGetContainerForData() {
		// IData data = new DataBasic(UUID.randomUUID().toString());
		// //FIXME:
		// //List<IContainer> list = _pipProxy.getContainerForData(data);
		// _logger.debug("Received result: " + Arrays.toString(list.toArray()));
		//
		// Assert.assertNotNull(list);
	}

	@Test
	public void testNotifyActualEvent() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		IEvent event = new EventBasic("x", Collections.<String, String> emptyMap());
		IStatus status = pip.update(event);
		_logger.debug("Received status: " + status);
		Assert.assertNotNull(status);
	}

	@Test
	public void testMultipleInvocations() {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
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

	@Test
	public void structureThriftTest() {
		IPLocation pipLocation = new IPLocation("localhost", PIP_SERVER_PORT);

		Pip2PipClient toPip = new ThriftClientFactory().createPip2PipClient(pipLocation);
		try {
			toPip.connect();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		/*
		 * Create new structure
		 *
		 * lbl1 --> A, B
		 *
		 * lbl2 --> C
		 *
		 * lbl3 --> empty
		 *
		 * lbl4 --> A, B, C
		 */

		HashMap<String,Set<IData>> map= new HashMap<String,Set<IData>>();

		IData a = mf.createData();
		IData b = mf.createData();
		IData c = mf.createData();

		HashSet<IData> set1= new HashSet<IData>();
		HashSet<IData> set2= new HashSet<IData>();
		HashSet<IData> set3= new HashSet<IData>();

		set1.add(a);
		set1.add(b);
		set2.add(c);
		set3.add(a);
		set3.add(b);
		set3.add(c);

		map.put("lbl1", set1);
		map.put("lbl2", set2);
		map.put("lbl3", new HashSet<IData>());
		map.put("lbl4", set3);

		Map<String,Set<IData>> res=toPip.getStructureOf(a);
		Assert.assertNotNull(res);
		Assert.assertEquals(res.size(),0);


		IData structData = toPip.newStructuredData(map);
		Assert.assertNotNull(structData);

		res=toPip.getStructureOf(a);
		Assert.assertNotNull(res);
		Assert.assertEquals(res.size(),0);

		res=toPip.getStructureOf(structData);
		Assert.assertNotNull(res);
		_logger.debug("map="+res);
		Assert.assertEquals(res.containsKey("lbl1"), true);
		Assert.assertEquals(res.get("lbl1").contains(a), true);
		Assert.assertEquals(res.get("lbl1").contains(b), true);
		Assert.assertEquals(res.get("lbl1").contains(c), false);


		Set<IData> flat= toPip.flattenStructure(a);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), false);
		Assert.assertEquals(flat.contains(c), false);

		flat= toPip.flattenStructure(structData);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), true);
		Assert.assertEquals(flat.contains(c), true);
		Assert.assertEquals(flat.contains(structData), true);


		HashSet<IData> set4= new HashSet<IData>();
		HashSet<IData> set5= new HashSet<IData>();

		set4.add(a);
		set4.add(structData);
		set5.add(b);
		set5.add(structData);

		HashMap<String,Set<IData>> map2= new HashMap<String,Set<IData>>();
		map2.put("lbl1",set4);
		map2.put("lbl2",set5);
		IData structData2 = toPip.newStructuredData(map2);

		flat= toPip.flattenStructure(structData2);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), true);
		Assert.assertEquals(flat.contains(c), true);
		Assert.assertEquals(flat.contains(structData), true);

	}
}
