package de.tum.in.i22.cm.cm.in.pmp;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tum.in.i22.cm.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpString;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.in.i22.uc.cm.methods.EPmp2PdpMethod;

public abstract class PmpClientConnectionHandler extends ClientConnectionHandler {

	private final RequestHandler _requestHandler = RequestHandler.getInstance();

	public PmpClientConnectionHandler(InputStream inputStream, OutputStream outputStream) throws IOException {
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
		EPmp2PdpMethod method = EPmp2PdpMethod.fromByte(methodCodeBytes[0]);
		_logger.trace("Method to invoke: " + method);

		//parse message
		switch (method) {
			case DEPLOY_MECHANISM:
				doDeployMechanism();
				break;
			case EXPORT_MECHANISM:
				doExportMechanism();
				break;
			case REVOKE_MECHANISM:
				doRevokeMechanism();
				break;
			default:
				throw new RuntimeException("Method " + method + " is not supported.");
		}
	}

	private void doDeployMechanism()
			throws IOException, InterruptedException {

		_logger.debug("Do deploy mechanism");
		GpMechanism gpMechanism = GpMechanism.parseDelimitedFrom(getDataInputStream());
		if (gpMechanism != null) {
			_logger.trace("Received mechanism: " + gpMechanism);

			IMechanism mechanism = new MechanismBasic(gpMechanism);
			PmpRequest pmpRequest = new PmpRequest(EPmp2PdpMethod.DEPLOY_MECHANISM, mechanism);
			_requestHandler.addPmpRequest(pmpRequest, this);

			Object responseObj = waitForResponse();

			if (responseObj instanceof IStatus) {
				IStatus responseStatus = (IStatus)responseObj;
				GpStatus gpStatus = StatusBasic.createGpbStatus(responseStatus);
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
				throwAwayResponse();
				gpStatus.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();
			} else {
				throw new RuntimeException("EStatus type expected for " + responseObj);
			}
		} else {
			_logger.debug("Received mechanism is null.");
		}
	}

	private void doExportMechanism()
			throws IOException, InterruptedException {

		_logger.debug("Do export mechanism");
		GpString gpString = GpString.parseDelimitedFrom(getDataInputStream());
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
		else {
			_logger.error("No data received as string parameter");
		}
	}

	private void doRevokeMechanism()
			throws IOException, InterruptedException {

		_logger.debug("Do revoke mechanism");
		GpString gpString = GpString.parseDelimitedFrom(getDataInputStream());
		if (gpString != null) {
			_logger.trace("Received string parameter: " + gpString.getValue());
			String param = gpString.getValue();
			PmpRequest pmpRequest = new PmpRequest(EPmp2PdpMethod.REVOKE_MECHANISM, param);
			_requestHandler.addPmpRequest(pmpRequest, this);

			_logger.trace("Wait for EStatus response");
			Object responseObj = waitForResponse();

			if (responseObj instanceof IStatus) {
				IStatus responseStatus = (IStatus)responseObj;
				GpStatus gpStatus = StatusBasic.createGpbStatus(responseStatus);
				// it is crucial to call throwAwayResponse method at this point
				// it will set response to null so that pause/resume thread works correctly
				throwAwayResponse();
				gpStatus.writeDelimitedTo(getOutputStream());
				getOutputStream().flush();

			} else {
				throw new RuntimeException("EStatus type expected for " + responseObj);
			}
		}

	}
}
