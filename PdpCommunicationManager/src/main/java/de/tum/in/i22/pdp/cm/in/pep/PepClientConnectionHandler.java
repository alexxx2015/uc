package de.tum.in.i22.pdp.cm.in.pep;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.IMessageFactory;
import de.tum.in.i22.uc.cm.in.MessageFactory;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;

public class PepClientConnectionHandler extends ClientConnectionHandler {
	
	public PepClientConnectionHandler(Socket socket) {
		super(socket);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		
		_logger.debug("Do processing invoked in PEP client connection handler");
		// first byte is currently not used
		DataInputStream dis = getDataInputStream();
		dis.readFully(new byte[1]);
		
		// parse message
		GpEvent gpEvent = GpEvent.parseDelimitedFrom(getDataInputStream());
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
