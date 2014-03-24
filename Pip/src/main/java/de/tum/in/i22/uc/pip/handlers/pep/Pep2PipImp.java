package de.tum.in.i22.uc.pip.handlers.pep;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pip;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;

public abstract class Pep2PipImp extends Connection implements IPep2Pip {
	public Pep2PipImp(Connector connector) {
		super(connector);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
		return null;
	}
}
