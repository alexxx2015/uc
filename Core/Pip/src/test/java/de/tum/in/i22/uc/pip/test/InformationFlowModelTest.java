package de.tum.in.i22.uc.pip.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

public class InformationFlowModelTest {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

	private static final Logger _logger = LoggerFactory.getLogger(InformationFlowModelTest.class);

	private static final IBasicInformationFlowModel _ifModel = new InformationFlowModelManager();

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
		IContainer a = _messageFactory.createContainer();
		IContainer b = _messageFactory.createContainer();
		IContainer c = _messageFactory.createContainer();
		IContainer d = _messageFactory.createContainer();
		IContainer e = _messageFactory.createContainer();
		IContainer h = _messageFactory.createContainer();
		IContainer k = _messageFactory.createContainer();

		_ifModel.addAlias(a, b);
		_ifModel.addAlias(b, c);
		_ifModel.addAlias(b, d);
		_ifModel.addAlias(e, h);
		_ifModel.addAlias(b, h);
		_ifModel.addAlias(c, k);
		_ifModel.addAlias(b, a);

		Set<IContainer> expectedClosure = new HashSet<>(Arrays.asList(a, b, c, d, h, k));

		Set<IContainer> aliasClosure = _ifModel.getAliasTransitiveReflexiveClosure(a);
		_logger.debug(Arrays.toString(aliasClosure.toArray()));

		Assert.assertEquals(expectedClosure, aliasClosure);
	}

}
