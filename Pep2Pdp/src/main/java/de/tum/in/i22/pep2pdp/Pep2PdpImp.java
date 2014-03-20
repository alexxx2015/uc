package de.tum.in.i22.pep2pdp;

import java.io.IOException;
import java.io.OutputStream;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpByteArray;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpPipDeployer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpRegPxp;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.methods.EPep2PdpMethod;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.cm.util.GpUtil;

public class Pep2PdpImp extends Connection implements IPep2Pdp {
	public Pep2PdpImp(Connector connector) {
		super(connector);
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
 		_logger.debug("notifyEvent method entered");

		_logger.trace("Create Google Protocol Buffer event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		try {
			OutputStream out = getOutputStream();
			out.write(EPep2PdpMethod.NOTIFY_EVENT.getValue());

			gpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("Event written to OutputStream");

			_logger.trace("Wait for GpResponse");
			GpResponse gpResponse = GpResponse.parseDelimitedFrom(getInputStream());

			return new ResponseBasic(gpResponse);
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
			//TODO better throw custom unchecked exception than return null
			return null;
		}
	}


	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Auto-generated method stub
		_logger.debug("register PXP {}" + pxp.getId());

		PdpProtos.GpRegPxp.Builder gpEventBuilder = PdpProtos.GpRegPxp.newBuilder();
		gpEventBuilder.setDescription(pxp.getDescription());
		gpEventBuilder.setId(pxp.getId());
		gpEventBuilder.setIp(pxp.getIp());
		gpEventBuilder.setPort(pxp.getPort());
		GpRegPxp gpRegPxpEvent = gpEventBuilder.build();

		try {
			OutputStream out = getOutputStream();
			out.write(EPep2PdpMethod.REGISTER_PXP.getValue());
			gpRegPxpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("Event written to OutputStream");

			_logger.trace("Wait for GpResponse");
			GpBoolean response = GpBoolean.parseDelimitedFrom(getInputStream());
			return response.getValue();
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
		}

		return false;
	}

 	@Override
 	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
 			byte[] jarFileBytes,
 			EConflictResolution flagForTheConflictResolution) {

 		_logger.debug("updateInformationFlowSemantics method entered");

 		_logger.trace("Create Google Protocol Buffer intances");
 		GpPipDeployer gpPipDeployer = PipDeployerBasic.createGpPipDeployer(deployer);
 		GpByteArray gpByteArray = GpUtil.createGpByteString(jarFileBytes);
 		GpConflictResolutionFlag gpFlag = GpUtil.createGpConflictResolutionFlag(flagForTheConflictResolution);

 		try {
 			OutputStream out = getOutputStream();
 			out.write(EPep2PdpMethod.UPDATE_INFORMATION_FLOW_SEMANTICS.getValue());
 			gpPipDeployer.writeDelimitedTo(out);
 			gpByteArray.writeDelimitedTo(out);
 			gpFlag.writeDelimitedTo(out);
 			out.flush();
 			_logger.trace("deployer, jarFileBytes and flag written to OutputStream");

 			_logger.trace("Wait for GpStatus");
 			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
 			return new StatusBasic(gpStatus);
 		} catch (IOException ex) {
 			_logger.error("Failed to update information flow semantics.", ex);
 			//TODO better throw custom unchecked exception than return null
 			return null;
 		}
 	}
}
