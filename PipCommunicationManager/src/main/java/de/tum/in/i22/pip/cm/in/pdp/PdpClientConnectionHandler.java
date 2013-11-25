package de.tum.in.i22.pip.cm.in.pdp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
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
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.in.i22.uc.cm.util.GpUtil;

public class PdpClientConnectionHandler extends ClientConnectionHandler {
	private IPdp2Pip _pdp2pip = PipHandler.getInstance();

	protected PdpClientConnectionHandler(Socket socket) {
		super(socket);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		
		// first determine the method (operation) by reading the first byte
		DataInputStream dataInputStream = getDataInputStream();
		byte methodCodeBytes[] = new byte[1];
		dataInputStream.readFully(methodCodeBytes);
		
		EPdp2PipMethod method = EPdp2PipMethod.fromByte(methodCodeBytes[0]);
		_logger.trace("Method to invoke: " + method);
		
		switch (method) {
			case EVALUATE_PREDICATE: 
				doEvaluatePredicate();
				break;
			case GET_CONTAINER_FOR_DATA:
				doGetContainerForData();
				break;
			case GET_DATA_IN_CONTAINER:
				doGetDataInContainer();
				break;
			case NOTIFY_ACTUAL_EVENT:
				doNotifyActualEvent();
				break;
			case UPDATE_INFORMATION_FLOW_SEMANTICS:
				doUpdateInformationFlowSemantics();
				break;
			default:
				throw new RuntimeException("Method " + method + " is not supported.");
		}
	}

	private void doGetContainerForData() 
			throws IOException {
		_logger.debug("Do get container for data");
		GpData gpData = GpData.parseDelimitedFrom(getDataInputStream());
		assert(gpData != null);
		
		_logger.trace("Received data parameter: " + gpData);
		
		IData data = new DataBasic(gpData);
		
		Set<IContainer> containerForDataList = _pdp2pip.getContainerForData(data);
		_logger.trace("Return the list of containers for data");
		GpContainerList gpContainerList = GpUtil.convertToGpContainerList(new ArrayList<IContainer>(containerForDataList));
		gpContainerList.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}

	private void doGetDataInContainer() 
			throws IOException {
		_logger.debug("Do get data in container");
		GpContainer gpContainer = GpContainer.parseDelimitedFrom(getDataInputStream());
		assert(gpContainer != null);
		
		_logger.trace("Received container parameter: " + gpContainer);
		
		IContainer container = new ContainerBasic(gpContainer);
		
		Set<IData> dataInContainerList = _pdp2pip.getDataInContainer(container);
		_logger.trace("Return the list of data in container");
		GpDataList gpDataList = GpUtil.convertToGpList(new ArrayList<IData>(dataInContainerList));
		gpDataList.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}

	private void doEvaluatePredicate() 
			throws IOException {
		_logger.debug("Do evaluate predicate");
		GpEvent gpEvent = GpEvent.parseDelimitedFrom(getDataInputStream());
		GpString gpPredicate = GpString.parseDelimitedFrom(getDataInputStream());
		assert(gpEvent != null);
		assert(gpPredicate != null);
		
		_logger.trace("Received event parameter: " + gpEvent);
		_logger.trace("Received predicate parameter: " + gpPredicate);
		
		IEvent event = new EventBasic(gpEvent);
		boolean result = _pdp2pip.evaluatePredicate(event, gpPredicate.getValue());
		GpBoolean gpResult = GpUtil.createGpBoolean(result);
		gpResult.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}
	
	private void doNotifyActualEvent() 
			throws IOException {
		_logger.debug("Do notify actual event");
		GpEvent gpEvent = GpEvent.parseDelimitedFrom(getDataInputStream());
		assert(gpEvent != null);
		
		_logger.trace("Received event parameter: " + gpEvent);
		
		IEvent event = new EventBasic(gpEvent);
		IStatus status = _pdp2pip.notifyActualEvent(event);
		GpStatus gpStatus = StatusBasic.createGpbStatus(status);
		gpStatus.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}
	
	private void doUpdateInformationFlowSemantics() 
			throws IOException {
		
		_logger.debug("Do update information flow semantics");
		GpPipDeployer gpPipDeployer = GpPipDeployer.parseDelimitedFrom(getDataInputStream());
		GpByteArray gpByteArray = GpByteArray.parseDelimitedFrom(getDataInputStream());
		GpConflictResolutionFlag gpFlag = GpConflictResolutionFlag.parseDelimitedFrom(getDataInputStream());
		_logger.trace("Parameteres parsed");
		
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
		IPipDeployer pipDeployer = mf.createPipDeployer(gpPipDeployer);
		byte[] jarBytes = gpByteArray.getByteArray().toByteArray();
		
		EConflictResolution conflictResolutionFlag = EConflictResolution.convertFromGpEConflictResolution(gpFlag.getValue());
		_logger.trace("Parameters: " + pipDeployer + " " + conflictResolutionFlag);
		
		String fileName = "piptemp" + System.currentTimeMillis() + ".jar";
		
		File file = new File(FileUtils.getTempDirectory(), fileName);
		FileUtils.writeByteArrayToFile(file, jarBytes);
		_logger.trace("File name: " + file.getAbsolutePath());
		IStatus status = _pdp2pip.updateInformationFlowSemantics(pipDeployer, file, conflictResolutionFlag);
		file.delete();
		
		_logger.debug("Send status back to PDP. Status: " + status);
		GpStatus gpStatus = StatusBasic.createGpbStatus(status);
		gpStatus.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}
	
}
