package de.tum.in.i22.pdp.cm.in.pmp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.tum.in.i22.pdp.cm.in.EPmp2PdpMethod;
import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.cm.in.pep.ClientConnectionHandler;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class PmpClientConnectionHandler extends ClientConnectionHandler {
	
	private RequestHandler _requestHandler = RequestHandler.getInstance();
	
	public PmpClientConnectionHandler(Socket socket) {
		super(socket);
	}
	
	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException {
		
		// first determine the method (operation) by reading the first byte
		
		ObjectInputStream objInput = getObjectInputStream();
		
		byte methodCode = objInput.readByte();
		EPmp2PdpMethod method = EPmp2PdpMethod.fromByte(methodCode);
		
		int messageSize = objInput.readInt();
		//TODO use value from configuration file
		if (messageSize > 1024) {
			_logger.debug("Message size to big: " + messageSize);
			throw new RuntimeException("Message too big! Message size: " + messageSize);
		}
		
		byte[] bytes = new byte[messageSize];
		objInput.readFully(bytes);
		
		//parse message
		switch (method) {
			case DEPLOY_MECHANISM:
				doDeployMechanism(bytes);
				break;
			case EXPORT_MECHANISM:
				break;
			case REVOKE_MECHANISM:
				break;
			default:
				throw new RuntimeException("Method " + method + " is unsupported.");
		}
	}
	
	private void doDeployMechanism(byte[] bytes) throws IOException, InterruptedException {
		GpMechanism gpMechanism = GpMechanism.parseFrom(bytes);					
		if (gpMechanism != null) {
			_logger.trace("Received mechanism: " + gpMechanism);
			
			IMechanism mechanism = new MechanismBasic(gpMechanism);
			PmpRequest pmpRequest = new PmpRequest(EPmp2PdpMethod.DEPLOY_MECHANISM, mechanism);
			_requestHandler.addPmpRequest(pmpRequest, this);
			
			Object responseObj = waitForResponse();
			
			if (responseObj instanceof EStatus) {
				EStatus response = (EStatus)responseObj;
				GpStatus.Builder status = GpStatus.newBuilder();
				status.setValue(response);
				// absolutely must call
				throwAwayResponse();
				status.build().writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			} else {
				throw new RuntimeException("EStatus type expected for " + responseObj);
			}
		} else {
			_logger.debug("Received mechanism is null.");
		}
	}
	
}
