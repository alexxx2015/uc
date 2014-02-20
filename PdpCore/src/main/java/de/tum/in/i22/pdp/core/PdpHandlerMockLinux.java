package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PdpHandlerMockLinux implements IIncoming {

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
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
	public HashMap<String, ArrayList<Mechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

}
