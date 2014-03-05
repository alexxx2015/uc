package de.tum.in.i22.pep2pdp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpByteArray;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpPipDeployer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.in.EPep2PdpMethod;
import de.tum.in.i22.uc.cm.out.PipeConnector;
import de.tum.in.i22.uc.cm.util.GpUtil;

public class Pep2PdpPipeImp extends PipeConnector implements IPep2PdpPipe {


	private static final Logger _logger = Logger
			.getLogger(Pep2PdpPipeImp.class);

//	private static IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

 	public Pep2PdpPipeImp(File inPipe, File outPipe) {
 		super(inPipe, outPipe);
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
