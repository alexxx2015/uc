package de.tum.in.i22.uc.cm.client;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Any;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pip;

public class Pep2AnyClient implements IPep2Any {

	protected IPep2Pdp _pdp;
	protected IPep2Pip _pip;

	public Pep2AnyClient() {
	}

	public Pep2AnyClient(IPep2Pdp pdp, IPep2Pip pip) {
		_pdp = pdp;
		_pip = pip;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_pdp.notifyEventAsync(event);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		return _pdp.notifyEventSync(event);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution flagForTheConflictResolution) {
		return _pip.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

}
