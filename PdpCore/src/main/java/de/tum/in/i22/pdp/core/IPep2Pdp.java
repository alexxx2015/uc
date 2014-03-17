package de.tum.in.i22.pdp.core;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.IConnection;

public interface IPep2Pdp extends IConnection {
	public IResponse notifyEvent(IEvent event);

	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution);
}
