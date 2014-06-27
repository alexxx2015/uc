package de.tum.in.i22.uc.cm.processing.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
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
	public void newPolicy(XmlPolicy xmlPolicy) {
		// TODO Auto-generated method stub
		_logger.error("DummyDistributionManager DUMMY Implementation");
		_logger.error("newPolicy method invoked");
	}


}
