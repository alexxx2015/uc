package de.tum.in.i22.pdp.cm.in.pep;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;

public class PepClientConnectionHandler extends ClientConnectionHandler {
	
	public PepClientConnectionHandler(Socket socket) {
		super(socket);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		
		int messageSize = getObjectInputStream().readInt();
		if (messageSize > PdpSettings.getMaxPepToPdpMessageSize()) {
			_logger.debug("Message size to big: " + messageSize);
			throw new MessageTooLargeException("Message too big! Message size: "
					+ messageSize);
		}

		byte[] bytes = new byte[messageSize];
		getObjectInputStream().readFully(bytes);
		// parse message
		GpEvent gpEvent = GpEvent.parseFrom(bytes);
		if (gpEvent != null) {
			_logger.trace("Received event: " + gpEvent);

			RequestHandler requestHandler = RequestHandler.getInstance();

			IMessageFactory mf = MessageFactory.getInstance();
			IEvent event = mf.createEvent(gpEvent, System.currentTimeMillis());
			requestHandler.addEvent(event, this);

			Object responseObj = waitForResponse();
			
			if (responseObj instanceof IResponse) {
				IResponse response = (IResponse)responseObj;
				_logger.trace("Response to return: " + response);

				GpResponse gpResponse = ResponseBasic.createGpbResponse(response);
				// important!!! Make sure it is inovoked in all classes
				throwAwayResponse();
				gpResponse.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			} else {
				throw new RuntimeException("IResponse type expected for " + responseObj);
			}
		} else {
			_logger.debug("Received event is null.");
		}
	}
}
