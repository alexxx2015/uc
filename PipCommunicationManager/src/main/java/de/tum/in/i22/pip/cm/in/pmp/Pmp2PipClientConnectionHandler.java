package de.tum.in.i22.pip.cm.in.pmp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;

public class Pmp2PipClientConnectionHandler extends ClientConnectionHandler {
	private IPmp2Pip _pmp2pip = Pmp2Pip.getInstance();

	protected Pmp2PipClientConnectionHandler(Socket socket) {
		super(socket);
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		
		// first determine the method (operation) by reading the first byte
		DataInputStream dataInputStream = getDataInputStream();
		// currently there is only one method, so the first byte is not used
		byte methodCodeBytes[] = new byte[1];
		dataInputStream.readFully(methodCodeBytes);
		
		doInitialRepresentation();
	}

	private void doInitialRepresentation() 
			throws IOException {
		_logger.debug("Do Initial Representation");
		DataInputStream input = getDataInputStream();
		GpContainer gpContainer = GpContainer.parseDelimitedFrom(input);
		GpData gpData = GpData.parseDelimitedFrom(input);
		assert(gpContainer != null);
		assert(gpData != null);
		
		_logger.trace("Received container parameter: " + gpContainer);
		_logger.trace("Received data parameter: " + gpData);
		
		IContainer container = new ContainerBasic(gpContainer);
		IData data = new DataBasic(gpData);
		
		IStatus status = _pmp2pip.initialRepresentation(container, data);
		_logger.trace("Return status: " + status);
		GpStatus gpStatus = StatusBasic.createGpbStatus(status);
		gpStatus.writeDelimitedTo(getOutputStream());
		getOutputStream().flush();
	}
}
