package de.tum.in.i22.pip.handlers.pdp;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pip;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;

public class Pep2PipImp extends Connection implements IPep2Pip {
	public Pep2PipImp(Connector connector) {
		super(connector);
	}

 	@Override
 	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
 			byte[] jarFileBytes,
 			EConflictResolution flagForTheConflictResolution) {
//
// 		_logger.debug("updateInformationFlowSemantics method entered");
//
// 		_logger.trace("Create Google Protocol Buffer intances");
// 		GpPipDeployer gpPipDeployer = PipDeployerBasic.createGpPipDeployer(deployer);
// 		GpByteArray gpByteArray = GpUtil.createGpByteString(jarFileBytes);
// 		GpConflictResolutionFlag gpFlag = GpUtil.createGpConflictResolutionFlag(flagForTheConflictResolution);
//
// 		try {
// 			OutputStream out = getOutputStream();
// 			out.write(EPep2PdpMethod.UPDATE_INFORMATION_FLOW_SEMANTICS.getValue());
// 			gpPipDeployer.writeDelimitedTo(out);
// 			gpByteArray.writeDelimitedTo(out);
// 			gpFlag.writeDelimitedTo(out);
// 			out.flush();
// 			_logger.trace("deployer, jarFileBytes and flag written to OutputStream");
//
// 			_logger.trace("Wait for GpStatus");
// 			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
// 			return new StatusBasic(gpStatus);
// 		} catch (IOException ex) {
// 			_logger.error("Failed to update information flow semantics.", ex);
// 			//TODO better throw custom unchecked exception than return null
 			return null;
// 		}
 	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
		return null;
	}
}
