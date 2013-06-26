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
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;

public class PmpClientConnectionHandler extends ClientConnectionHandler {
	
	private RequestHandler _requestHandler = RequestHandler.getInstance();
	
	public PmpClientConnectionHandler(Socket socket) {
		super(socket);
	}
	
	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException {
		
		// first determine the method (operation) by reading the first byte
		_logger.trace("Process the incomming bytes");
		ObjectInputStream objInput = getObjectInputStream();
		
		byte methodCode = objInput.readByte();
		EPmp2PdpMethod method = EPmp2PdpMethod.fromByte(methodCode);
		_logger.trace("Method to invoke: " + method);
		
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
				doExportMechanism(bytes);
				break;
			case REVOKE_MECHANISM:
				doRevokeMechanism(bytes);
				break;
			default:
				throw new RuntimeException("Method " + method + " is not supported.");
		}
	}
	
	private void doDeployMechanism(byte[] bytes)
			throws IOException, InterruptedException {
		
		_logger.debug("Do deploy mechanism");
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
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
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
	
	private void doExportMechanism(byte[] bytes)
			throws IOException, InterruptedException {
		
		_logger.debug("Do export mechanism");
		GpString gpString = GpString.parseFrom(bytes);
		if (gpString != null) {
			_logger.trace("Received string parameter: " + gpString.getValue());
			String param = gpString.getValue();
			PmpRequest pmpRequest = new PmpRequest(EPmp2PdpMethod.EXPORT_MECHANISM, param);
			_requestHandler.addPmpRequest(pmpRequest, this);
			
			_logger.trace("Wait for IMechanism response");
			Object responseObj = waitForResponse();
			
			if (responseObj instanceof IMechanism) {
				IMechanism mechanism = (IMechanism)responseObj;
				GpMechanism gpMechanism = MechanismBasic.createGpbMechanism(mechanism);
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
				throwAwayResponse();
				gpMechanism.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
				
			} else {
				throw new RuntimeException("IMechanism type expected for " + responseObj);
			}
		}
	}
	
	private void doRevokeMechanism(byte[] bytes)
			throws IOException, InterruptedException {
		
		_logger.debug("Do revoke mechanism");
		GpString gpString = GpString.parseFrom(bytes);
		if (gpString != null) {
			_logger.trace("Received string parameter: " + gpString.getValue());
			String param = gpString.getValue();
			PmpRequest pmpRequest = new PmpRequest(EPmp2PdpMethod.REVOKE_MECHANISM, param);
			_requestHandler.addPmpRequest(pmpRequest, this);
			
			_logger.trace("Wait for EStatus response");
			Object responseObj = waitForResponse();
			
			if (responseObj instanceof EStatus) {
				EStatus response = (EStatus)responseObj;
				GpStatus.Builder status = GpStatus.newBuilder();
				status.setValue(response);
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
				throwAwayResponse();
				status.build().writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
				
			} else {
				throw new RuntimeException("EStatus type expected for " + responseObj);
			}
		}
		
	}
	
}
