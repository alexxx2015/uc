package de.tum.in.i22.pdp.cm.in.pep;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
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
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.EPep2PdpMethod;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;

public abstract class PepClientConnectionHandler extends ClientConnectionHandler {

	public PepClientConnectionHandler(DataInputStream inputStream, OutputStream outputStream) {
		super(inputStream, outputStream);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {

		_logger.debug("Do processing invoked in PEP client connection handler");
		// first byte is currently not used
		DataInputStream dis = getDataInputStream();
		byte methodCodeBytes[] = new byte[1];
		dis.readFully(methodCodeBytes);
		EPep2PdpMethod method = EPep2PdpMethod.fromByte(methodCodeBytes[0]);
		_logger.trace("Method to invoke: " + method);

		switch (method) {
		case NOTIFY_EVENT:
			doNotifyEvent();
			break;
		case UPDATE_INFORMATION_FLOW_SEMANTICS:
			doUpdateInformationFlowSemantics();
			break;
		default:
			throw new RuntimeException("Method " + method
					+ " is not supported.");
		}

	}

	private void doUpdateInformationFlowSemantics()
			throws IOException, InterruptedException {

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

		RequestHandler requestHandler = RequestHandler.getInstance();
		requestHandler.addUpdateIfFlowRequest(pipDeployer, jarBytes, conflictResolutionFlag, this);

		Object responseObj = waitForResponse();

		if (responseObj instanceof IStatus) {
			IStatus status = (IStatus) responseObj;
			_logger.trace("Status to return: " + status);

			GpStatus gpStatus = StatusBasic.createGpbStatus(status);
			// important!!! Make sure it is invoked in all classes
			throwAwayResponse();
			gpStatus.writeDelimitedTo(getOutputStream());
			getOutputStream().flush();
		} else {
			throw new RuntimeException("IStatus type expected for "
					+ responseObj);
		}
	}

	private void doNotifyEvent()
			throws IOException, InterruptedException {

		_logger.debug("Do notify event");
		GpEvent gpEvent = GpEvent.parseDelimitedFrom(getDataInputStream());
		if (gpEvent != null) {
			_logger.trace("Received event: " + gpEvent);

			RequestHandler requestHandler = RequestHandler.getInstance();

			IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
			IEvent event = mf.createEvent(gpEvent, System.currentTimeMillis());
			requestHandler.addEvent(event, this);

			Object responseObj = waitForResponse();

			if (responseObj instanceof IResponse) {
				IResponse response = (IResponse) responseObj;
				_logger.trace("Response to return: " + response);

				GpResponse gpResponse = ResponseBasic
						.createGpbResponse(response);
				// important!!! Make sure it is invoked in all classes
				throwAwayResponse();
				gpResponse.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			} else {
				throw new RuntimeException("IResponse type expected for "
						+ responseObj);
			}
		} else {
			_logger.debug("Received event is null.");
		}
	}
}
