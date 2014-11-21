package de.tum.in.i22.uc.pip.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.pip.ifm.IBasicInformationFlowModel;
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

	@Test
	public void getAllNamesTest() {
		_ifModel.addName(new NameBasic("basic1"), new ContainerBasic("c1"));
		_ifModel.addName(new NameBasic("basic2"), new ContainerBasic("c1"));
		_ifModel.addName(new NameBasic("basic3"), new ContainerBasic("c1"));
		_ifModel.addName(new NameBasic("basic4"), new ContainerBasic("c1"));
		_ifModel.addName(new NameBasic("basic5"), new ContainerBasic("c1"));

		_ifModel.addName(FilenameName.create("host1", "file1"), new ContainerBasic("c1"));
		_ifModel.addName(FilenameName.create("host1", "file2"), new ContainerBasic("c1"));
		_ifModel.addName(FilenameName.create("host1", "file3"), new ContainerBasic("c1"));
		_ifModel.addName(FilenameName.create("host1", "file4"), new ContainerBasic("c1"));

		_ifModel.addName(ProcessName.create("host2", 531), new ContainerBasic("p1"));
		_ifModel.addName(ProcessName.create("host2", 532), new ContainerBasic("p2"));
		_ifModel.addName(ProcessName.create("host2", 533), new ContainerBasic("p3"));

		Assert.assertEquals(12, _ifModel.getAllNames(NameBasic.class).size());
		Assert.assertEquals(4, _ifModel.getAllNames(FilenameName.class).size());
		Assert.assertEquals(3, _ifModel.getAllNames(ProcessName.class).size());

		Assert.assertEquals(9, _ifModel.getAllNames(new ContainerBasic("c1")).size());

		Stopwatch watch = Stopwatch.createUnstarted();
		for (int i = 0; i < 9999999; i++) {
			watch.start();
			_ifModel.getAllNames(new ContainerBasic("c1"));
			watch.stop();
		}
		System.out.println(watch.elapsed(TimeUnit.MILLISECONDS));
	}
}
