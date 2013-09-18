package de.tum.in.i22.pip.core.manager;

import java.io.File;



public interface IPipManager {
	public void updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);
}	
