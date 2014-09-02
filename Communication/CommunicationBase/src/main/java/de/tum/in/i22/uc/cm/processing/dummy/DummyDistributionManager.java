package de.tum.in.i22.uc.cm.processing.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class DummyDistributionManager implements IDistributionManager {
	private static Logger _logger = LoggerFactory.getLogger(DummyDistributionManager.class);


	@Override
	public void init(PdpProcessor _pdp, PipProcessor _pip, PmpProcessor _pmp) {
		// TODO Auto-generated method stub
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("init method invoked");
	}

	@Override
	public void dataTransfer(RemoteDataFlowInfo dataflow) {
		// TODO Auto-generated method stub
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("dataTransfer method invoked");
	}

	@Override
	public void registerPolicy(String policyName) {
		// TODO Auto-generated method stub
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("register method invoked");
	}

	@Override
	public void update(IResponse response) {
		// TODO Auto-generated method stub
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("update method invoked");
	}

	@Override
	public boolean wasTrueSince(LiteralOperator operator, long since) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("wasObservedSince method invoked");
		return false;
	}

	@Override
	public boolean wasTrueInBetween(LiteralOperator operator, long from, long to) {
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("wasObservedInBetween method invoked");
		return true;
	}

}
