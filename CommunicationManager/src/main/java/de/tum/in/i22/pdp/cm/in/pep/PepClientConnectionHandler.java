package de.tum.in.i22.pdp.cm.in.pep;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.pdp.cm.in.EventHandler;
import de.tum.in.i22.pdp.cm.in.IForwarder;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ResponseBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;

public class PepClientConnectionHandler extends ClientConnectionHandler
		implements IForwarder {
	private IResponse _response = null;

	public PepClientConnectionHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void forwardResponse(IResponse response) {
		_logger.debug("Wake up the thread.");
		synchronized (this) {
			_response = (IResponse) response;
			notifyAll();
		}
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException {
		
		int messageSize = getObjectInputStream().readInt();
		if (messageSize > 1024) {
			_logger.debug("Message size to big: " + messageSize);
			throw new RuntimeException("Message too big! Message size: "
					+ messageSize);
		}

		byte[] bytes = new byte[messageSize];
		getObjectInputStream().readFully(bytes);
		// parse message
		GpEvent gpEvent = GpEvent.parseFrom(bytes);
		if (gpEvent != null) {
			_logger.trace("Received event: " + gpEvent);

			EventHandler eventHandler = EventHandler.getInstance();

			IMessageFactory mf = MessageFactory.getInstance();
			IEvent event = mf.createEvent(gpEvent, System.currentTimeMillis());
			eventHandler.addEvent(event, this);

			synchronized (this) {
				while (_response == null) {
					_logger.debug("Wait for the event to be processed.");
					wait();
				}
			}

			_logger.trace("Response to return: " + _response);

			GpResponse gpResponse = ResponseBasic.createGpbResponse(_response);
			_response = null;
			gpResponse.writeDelimitedTo(getOutputStream());
			getOutputStream().flush();
		} else {
			_logger.debug("Received event is null.");
		}
	}
}
