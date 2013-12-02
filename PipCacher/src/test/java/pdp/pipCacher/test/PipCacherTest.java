package pdp.pipCacher.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.pdp.pipcacher.PipCacherImpl;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.KeyBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
//import de.tum.in.i22.pip.core.PipHandlerMock;

public class PipCacherTest {
/*	private static final Logger _logger = Logger.getLogger(PipCacherTest.class);
	
	private static IPdpCore2PipCacher _core2pip;
	private static IPdpEngine2PipCacher _engine2pip;
	private static IPipCacher2Pip _pipHandler;
	
	private static IMessageFactory _messageFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		_pipHandler = new PipHandlerMock();
		_core2pip= new PipCacherImpl(_pipHandler);
		_engine2pip= (PipCacherImpl) _core2pip;
		
		_messageFactory = MessageFactoryCreator.createMessageFactory();
		
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
	public void testEvaluateSimulation() {
		
		//Add exemplary predicate
		Map <String,IKey> predicates=new HashMap<String,IKey>();
		IKey k = KeyBasic.createNewKey();
		predicates.put("isNotIn(TEST_D,TEST_C)", k);
		_core2pip.addPredicates(predicates);
				
		//Initialize if model with TEST_C --> TEST_D
		IEvent initEvent = _messageFactory.createActualEvent("SchemaInitializer", null);
		_pipHandler.notifyActualEvent(initEvent);

		//Test refresh
		IEvent updateDesiredEvent = _messageFactory.createDesiredEvent("SchemaUpdater", null);
		_core2pip.refresh(updateDesiredEvent);
		
		//Evaluate formula in simulated state
		Boolean b=_engine2pip.eval(k);
		assert(b!=null);
		Assert.assertEquals((boolean)b, true);
		
		IEvent updateActualEvent = _messageFactory.createActualEvent("SchemaUpdater", null);
		_pipHandler.notifyActualEvent(updateActualEvent);
		
		Assert.assertEquals((boolean)b,true);


		///TODO: finish writing it
		
	}
	
*/

}
