package de.tum.in.i22.uc.cm.interfaces;

import java.io.File;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PEP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
public interface IPep2Pip {
	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);
}
