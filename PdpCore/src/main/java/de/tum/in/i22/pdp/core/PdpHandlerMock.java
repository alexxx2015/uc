package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * This is just a stub for now
 *
 * @author Stoimenov
 *
 */
public class PdpHandlerMock implements IIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandlerMock.class);

//	private IPdpCore2PipCacher _pipCacher = PipCacherImpl.getReference();

	public PdpHandlerMock() {
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
		// TODO implement
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		// TODO implement
		_logger.debug("Notify event called");
		return DummyMessageGen.createRandomResponse();
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
		// leave empty
		// this method is never called
		// instead PDP delegates it to PIP
		return null;
	}

	@Override
	public IStatus setPdpCore2PipCacher(IPdpCore2PipCacher core2cacher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus setPdpEngine2PipCacher(IPdpEngine2PipCacher engine2cacher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentPipModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
