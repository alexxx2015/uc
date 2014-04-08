package de.tum.in.i22.uc.pep;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Any;
import de.tum.in.i22.uc.cm.server.IRequestHandler;

public class LocalPep implements IPep2Any {

	private final IRequestHandler _requestHandler;

	public LocalPep(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_requestHandler.notifyEventAsync(event);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		return _requestHandler.notifyEventSync(event);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution flagForTheConflictResolution) {
		return _requestHandler.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

}
