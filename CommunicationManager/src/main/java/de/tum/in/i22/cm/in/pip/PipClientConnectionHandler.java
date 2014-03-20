package de.tum.in.i22.cm.in.pip;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tum.in.i22.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.in.i22.uc.cm.methods.EPipRequestMethod;
import de.tum.in.i22.uc.cm.methods.EPipResponse;

public abstract class PipClientConnectionHandler extends ClientConnectionHandler {

	private final RequestHandler _requestHandler = RequestHandler.getInstance();

	public PipClientConnectionHandler(InputStream inputStream, OutputStream outputStream) throws IOException {
		super(inputStream, outputStream);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {

		// first determine the method (operation) by reading the first byte
		_logger.trace("Process the incomming bytes");
		DataInputStream dataInputStream = getDataInputStream();
		byte methodCodeBytes[] = new byte[1];
		dataInputStream.readFully(methodCodeBytes);
		EPipRequestMethod method = EPipRequestMethod.fromByte(methodCodeBytes[0]);
		_logger.trace("Method to invoke: " + method);

		//parse message
		switch (method) {
			case HAS_ALL_DATA:
				break;
			case HAS_ANY_DATA:
				break;
			case HAS_ALL_CONTAINERS:
				break;
			case HAS_ANY_CONTAINER:
				break;
			case NOTIFY_DATA_TRANSFER:
				break;
			case NOTIFY_ACTUAL_EVENT:
				doNotifyEvent();
				break;
			default:
				throw new RuntimeException("Method " + method + " is not supported.");
		}
	}

	private void doNotifyEvent() throws IOException, InterruptedException {
		_logger.debug("Do notify event.");

		DataInputStream dataInputStream = getDataInputStream();

		// read event type
		GpEvent gpEvent = GpEvent.parseDelimitedFrom(dataInputStream);

		// read second byte to determine expected response type
		byte methodCodeBytes[] = new byte[1];
		dataInputStream.readFully(methodCodeBytes);
		EPipResponse responseType = EPipResponse.fromByte(methodCodeBytes[0]);

		if (gpEvent != null && responseType != null) {
			_logger.trace("Received event: " + gpEvent + "; expects response: " + responseType);

			IEvent event = new EventBasic(gpEvent);
			PipRequest pipRequest = new PipRequest(event, responseType);
			_requestHandler.addPipRequest(pipRequest, this);

			Object responseObj = waitForResponse();

			if (responseObj instanceof IContainer && responseType == EPipResponse.ICONTAINER) {
				IContainer responseContainer = (IContainer)responseObj;
				GpContainer gpContainer = ContainerBasic.createGpbContainer(responseContainer);
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
				throwAwayResponse();
				gpContainer.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			}
			else if (responseObj instanceof IStatus && responseType == EPipResponse.ISTATUS) {
				IStatus responseStatus = (IStatus) responseObj;
				GpStatus gpStatus = StatusBasic.createGpbStatus(responseStatus);
				throwAwayResponse();
				gpStatus.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			}
			else if (responseObj == null && responseType == EPipResponse.VOID) {
				// TODO Can it be in fact null?
			}
			else {
				throw new RuntimeException("Invalid response: " + responseObj);
			}
		}
		else {
			_logger.debug("Received event is null.");
		}
	}
}
