package de.tum.in.i22.pdp.cm.out.pip;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.tum.in.i22.pip.cm.in.pdp.EPdp2PipMethod;
import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.uc.cm.AbstractConnection;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpByteArray;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpConflictResolutionFlag;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainerList;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpDataList;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpPipDeployer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpString;
import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.cm.util.GpUtil;

public abstract class Pdp2PipImp extends AbstractConnection implements IPdp2Pip {

	private static final Logger _logger = Logger.getLogger(Pdp2PipImp.class);

	private final IMessageFactory _mf = MessageFactoryCreator.createMessageFactory();

	public Pdp2PipImp(Connector connector) {
		super(connector);
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		_logger.debug("Evaluate predicate invoked");
		_logger.trace("Create Google Protocol Buffer Event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		_logger.trace("Create Google Protocol Buffer String instance");
		GpString gpPredicate = GpUtil.createGpString(predicate);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.EVALUATE_PREDICATE.getValue());
			gpEvent.writeDelimitedTo(out);
			gpPredicate.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpEvent and GpPredicate written to OutputStream");

			_logger.trace("Wait for boolean result");
			GpBoolean gpBoolean = GpBoolean.parseDelimitedFrom(getInputStream());
			return gpBoolean.getValue();
		} catch (IOException e) {
			_logger.error("Evaluate predicate failed.", e);
			return null;
		}
	}

	@Override
	public Set<IContainer> getContainerForData(IData data) {
		_logger.debug("Get container for data invoked");
		_logger.trace("Create Gpb data instance");
		GpData gpData = DataBasic.createGpbData(data);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.GET_CONTAINER_FOR_DATA.getValue());
			gpData.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpData written to OutputStream");

			_logger.trace("Wait for containers");
			GpContainerList gpContainerList = GpContainerList.parseDelimitedFrom(getInputStream());
			return new HashSet<IContainer>(GpUtil.convertToList(gpContainerList));
		} catch (IOException e) {
			_logger.error("Get container for data failed.", e);
			return null;
		}
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		_logger.debug("Get data in container invoked");
		_logger.trace("Create Gpb container instance");
		GpContainer gpContainer = ContainerBasic.createGpbContainer(container);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.GET_DATA_IN_CONTAINER.getValue());
			gpContainer.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpContainer written to OutputStream");

			_logger.trace("Wait for data list");
			GpDataList gpDataList = GpDataList.parseDelimitedFrom(getInputStream());
			return new HashSet<IData>(GpUtil.convertToList(gpDataList));
		} catch (IOException e) {
			_logger.error("Get data in container failed.", e);
			return null;
		}
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		_logger.debug("Notify actual event invoked");
		_logger.trace("Create Gpb event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		try {
			OutputStream out = getOutputStream();
			out.write(EPdp2PipMethod.NOTIFY_ACTUAL_EVENT.getValue());
			gpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("GpEvent written to OutputStream");

			_logger.trace("Wait for status");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
			return new StatusBasic(gpStatus);
		} catch (IOException e) {
			_logger.error("Notify actual event failed.", e);
			return null;
		}
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		_logger.debug("Update information flow semantics invoked");

 		try {
 			_logger.trace("Create Google Protocol Buffer intances");
 	 		GpPipDeployer gpPipDeployer = PipDeployerBasic.createGpPipDeployer(deployer);
 	 		GpByteArray gpByteArray = GpUtil.createGpByteString(FileUtils.readFileToByteArray(jarFile));
 	 		GpConflictResolutionFlag gpFlag = GpUtil.createGpConflictResolutionFlag(flagForTheConflictResolution);

 			OutputStream out = getOutputStream();
 			out.write(EPdp2PipMethod.UPDATE_INFORMATION_FLOW_SEMANTICS.getValue());
 			gpPipDeployer.writeDelimitedTo(out);
 			gpByteArray.writeDelimitedTo(out);
 			gpFlag.writeDelimitedTo(out);
 			out.flush();
 			_logger.trace("deployer, jarFileBytes and flag written to OutputStream");

 			_logger.trace("Wait for Status");
 			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());
 			return new StatusBasic(gpStatus);
 		} catch (IOException ex) {
 			_logger.error("Failed to update information flow semantics.", ex);
 			return _mf.createStatus(EStatus.ERROR, "Error detected at PDP: " + ex.getMessage());
 		}
	}

	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO Auto-generated method stub
		return null;
	}
}
