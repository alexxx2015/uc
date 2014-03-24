package de.tum.in.i22.uc.cm.in.thrift;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Pxp;
import de.tum.i22.in.uc.cm.thrift.Response;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.in.i22.uc.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.requests.EPdpRequestType;
import de.tum.in.i22.uc.cm.requests.PdpRequest;

public class TAny2PdpHandler extends ClientConnectionHandler implements TAny2Pdp.Iface, IForwarder {

	private final int _port;

	public TAny2PdpHandler(int pepPort) {
		super(null, null);
		_port = pepPort;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " listening on port " + _port;
	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		_logger.debug("Thrift doProcessing invoked");

		// TODO Auto-generated method stub

	}

	@Override
	protected void disconnect() {
	}

	@Override
	public Response notifyEvent(Event e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: notifyEvent");
		return null;
	}

	@Override
	public boolean registerPxp(Pxp pxp) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: registerPxp");
		return false;
	}

	@Override
	public StatusType deployMechanism(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deploymech");
		return null;
	}

	@Override
	public StatusType revokeMechanism1(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech1");
		return null;
	}

	@Override
	public StatusType revokeMechanism2(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech2");
		return null;
	}

	@Override
	public StatusType deployPolicy(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deployPolicy");
		return null;
	}

	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: listMech");
		return null;
	}

}
