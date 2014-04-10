package de.tum.in.i22.uc.cm.interfaces;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PEP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPep2Pip")
public interface IPep2Pip {
	@AThriftMethod(signature="// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution conflictResolutionFlag)")
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution conflictResolutionFlag);
}
