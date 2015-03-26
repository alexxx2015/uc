package de.tum.in.i22.uc.cm.processing.dummy;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IDmp2Pmp;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.DmpProcessor;

public class DummyDmpProcessor extends DmpProcessor implements IDummyProcessor {
	private static Logger _logger = LoggerFactory.getLogger(DummyDmpProcessor.class);


	@Override
	public void init(IDmp2Pip pip, IDmp2Pmp pmp) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("init method invoked");
	}

	@Override
	public void doDataTransfer(RemoteDataFlowInfo dataflow) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("dataTransfer method invoked");
	}

	@Override
	public void register(XmlPolicy policy) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("register method invoked");
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("update method invoked");
	}

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

	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost,
			IName containerName, Set<IData> data) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("remoteTransfer method invoked");
		return new StatusBasic(EStatus.OKAY);
	}

}
