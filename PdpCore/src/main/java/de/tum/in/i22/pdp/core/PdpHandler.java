package de.tum.in.i22.pdp.core;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * This is just a stub for now
 * @author Stoimenov
 *
 */
public class PdpHandler
		implements IIncoming {
	
	
	private static final Logger _logger = Logger
			.getLogger(PdpHandler.class);
	
	private static PdpHandler _instance;

	
	public static PdpHandler getInstance() {
		if (_instance == null) {
			_instance = new PdpHandler();
		}
		return _instance;
	}
	
	private PdpHandler() {
		
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO implement
		_logger.debug("Deploy mechanism called");
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		_logger.debug("Export mechanism called");
		return DummyMessageGen.createMechanism();
	}

	@Override
	public IStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism called");
		//TODO implement
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		//TODO implement
		_logger.debug("Notify event called");
		return DummyMessageGen.createResponse();
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes, EConflictResolution flagForTheConflictResolution) {
		// leave empty
		// this method is never called
		// instead PDP delegates it to PIP
		return null;
	}
}
