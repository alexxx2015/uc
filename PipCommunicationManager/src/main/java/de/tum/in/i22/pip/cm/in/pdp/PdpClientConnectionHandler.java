package de.tum.in.i22.pip.cm.in.pdp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import de.tum.in.i22.pdp.cm.in.ClientConnectionHandler;
import de.tum.in.i22.pdp.cm.in.pep.MessageTooLargeException;
import de.tum.in.i22.pdp.cm.out.EPdp2PipMethod;
import de.tum.in.i22.pdp.cm.out.IPdp2Pip;
import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ContainerBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpContainerList;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpData;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataList;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;
import de.tum.in.i22.pdp.util.GpUtil;

public class PdpClientConnectionHandler extends ClientConnectionHandler {
	private IPdp2Pip _pdp2pip = Pdp2Pip.getInstance();

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
		
		List<IContainer> containerForDataList = _pdp2pip.getContainerForData(data);
		_logger.trace("Return the list of containers for data");
		GpContainerList gpContainerList = GpUtil.convertToGpContainerList(containerForDataList);
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
		
		List<IData> dataInContainerList = _pdp2pip.getDataInContainer(container);
		_logger.trace("Return the list of data in container");
		GpDataList gpDataList = GpUtil.convertToGpList(dataInContainerList);
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
		IResponse response = _pdp2pip.notifyActualEvent(event);
		GpResponse gpResponse = ResponseBasic.createGpbResponse(response);
		gpResponse.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}
	
}
