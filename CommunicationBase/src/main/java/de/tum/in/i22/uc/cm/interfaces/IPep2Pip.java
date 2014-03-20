/**
 * 
 */
package de.tum.in.i22.uc.cm.interfaces;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * @author Kelbert & Lovat
 *
 */
public interface IPep2Pip {
	
	
	
	// FIXME: No idea which one of these two is used. Superstar please fix it
	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);

	IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution);

}
