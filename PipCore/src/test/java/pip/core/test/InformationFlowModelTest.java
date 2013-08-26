package pip.core.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pip.core.InformationFlowModel;

public class InformationFlowModelTest {
	
	
	private static final Logger _logger = Logger
			.getLogger(InformationFlowModelTest.class);
	
	private static final InformationFlowModel _ifModel = InformationFlowModel.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

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
	public void test() {
		_ifModel.addAlias("A", "B");
		_ifModel.addAlias("B", "C");
		_ifModel.addAlias("B", "D");
		_ifModel.addAlias("E", "H");
		_ifModel.addAlias("B", "H");
		_ifModel.addAlias("C", "K");
		
		Set<String> expectedClosure = new HashSet<>(Arrays.asList("A", "B", "C", "D", "H", "K"));
		
		Set<String> aliasClosure = _ifModel.getAliasClosure("A");
		_logger.debug(Arrays.toString(aliasClosure.toArray()));
		
		Assert.assertEquals(expectedClosure, aliasClosure);
	}

}
