package de.tum.in.i22.uc.cm.processing.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DummyDistributionManager implements IDistributionManager {
	private static Logger _logger = LoggerFactory.getLogger(DummyDistributionManager.class);


	@Override
	public void init(PdpProcessor _pdp, PipProcessor _pip, PmpProcessor _pmp) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("init method invoked");
	}

	@Override
	public void dataTransfer(RemoteDataFlowInfo dataflow) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("dataTransfer method invoked");
	}

	@Override
	public void register(XmlPolicy policy, String from) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("register method invoked");
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("update method invoked");
	}

//	@Override
//	public boolean wasNotifiedSince(AtomicOperator operator, long since) {
//		_logger.error("DummyDistributionManager DUMMY Implementation");
//		_logger.error("wasObservedSince method invoked");
//		return false;
//	}
//
//	@Override
//	public boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to) {
//		_logger.error("DummyDistributionManager DUMMY Implementation");
//		_logger.error("wasObservedInBetween method invoked");
//		return true;
//	}

	@Override
	public void deregister(String policyName, IPLocation location) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("unregisterPolicy method invoked");
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("getResponsibleLocation method invoked");
		return IPLocation.localIpLocation;
	}

//	@Override
//	public int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to) {
//		_logger.error("DummyDistributionManager DUMMY Implementation");
//		_logger.error("howOftenTrueInBetween method invoked");
//		return 0;
//	}

	@Override
	public void setFirstTick(String policyName, String mechanismName, long firstTick) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("registerMechanism method invoked");
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("getFirstTick method invoked");
		return Long.MIN_VALUE;
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("wasNotifiedAtTimestep method invoked");
		return false;
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("howOftenNotifiedAtTimestep method invoked");
		return 0;
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("howOftenNotifiedSinceTimestep method invoked");
		return 0;
	}

	public void awaitPolicyTransfer(String policyName) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("awaitPolicyTransfer method invoked");
	}

}
