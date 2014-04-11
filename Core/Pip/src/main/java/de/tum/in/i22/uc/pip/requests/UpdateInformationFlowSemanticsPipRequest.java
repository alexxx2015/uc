package de.tum.in.i22.uc.pip.requests;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class UpdateInformationFlowSemanticsPipRequest extends PipRequest<IStatus> {
	private final IPipDeployer _deployer;
	private final File _jarFile;
	private final EConflictResolution _conflictResolutionFlag;

	public UpdateInformationFlowSemanticsPipRequest(IPipDeployer deployer, File jarFile, EConflictResolution conflictResolutionFlag) {
		_deployer = deployer;
		_jarFile = jarFile;
		_conflictResolutionFlag = conflictResolutionFlag;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.updateInformationFlowSemantics(_deployer, _jarFile, _conflictResolutionFlag);
	}

}
