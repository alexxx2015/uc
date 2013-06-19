package de.tum.in.i22.pdp.cm.in.pmp;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.pdp.cm.in.pep.ClientConnectionHandler;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class PmpClientConnectionHandler extends ClientConnectionHandler {
	
	public PmpClientConnectionHandler(Socket socket) {
		super(socket);
	}
	
	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException {
		int messageSize = getObjectInputStream().readInt();
		if (messageSize > 1024) {
			_logger.debug("Message size to big: " + messageSize);
			throw new RuntimeException("Message too big! Message size: " + messageSize);
		}
		
		byte[] bytes = new byte[messageSize];
		getObjectInputStream().readFully(bytes);
		//parse message
		GpMechanism gpMechanism = GpMechanism.parseFrom(bytes);					
		if (gpMechanism != null) {
			_logger.trace("Received mechanism: " + gpMechanism);
			// TODO pause EventHandler and invoke operation
			GpStatus.Builder status = GpStatus.newBuilder();
			status.setValue(EStatus.OKAY);
			status.build().writeDelimitedTo(getOutputStream());
			getOutputStream().flush();
		} else {
			_logger.debug("Received event is null.");
		}
	}
	
}
