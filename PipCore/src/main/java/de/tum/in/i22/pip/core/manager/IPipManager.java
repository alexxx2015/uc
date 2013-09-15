package de.tum.in.i22.pip.core.manager;

import java.io.File;



public interface IPipManager {
	// jar file
	public void updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);
}	
