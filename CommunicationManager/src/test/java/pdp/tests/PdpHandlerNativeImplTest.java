//package pdp.tests;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//
//import de.tum.in.i22.core.IIncoming;
//import de.tum.in.i22.injection.PdpModule;
//
//public class PdpHandlerNativeImplTest {
//	
//	//private static PdpHandlerPdpNative _pdpHandler;
//	private static IIncoming _pdpHandler;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		
//		Injector injector = Guice.createInjector(new PdpModule());
//
//		/*
//		 * Now that we've got the injector, we can build objects.
//		 */
//		//_pdpHandler = injector.getInstance(PdpHandlerPdpNative.class);
//		_pdpHandler = injector.getInstance(IIncoming.class);
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void test() {
//
////		Map<String, String> map = new HashMap<>();
////		map.put("val1", "value1");
////		map.put("val2", "value2");
////		IResponse response = _pdpHandler.notifyEvent(new EventBasic("testEvent", map, false));
////		
////		if (response == null) {
////			Assert.fail("Expected response different than null.");
////		}
//	}
//
//}
