package de.tum.in.i22.uc.cm.in.thrift;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;



public class TAny2PmpHandler extends ClientConnectionHandler implements TAny2Pmp.Iface, IForwarder {

	private final int _port;

	public TAny2PmpHandler(int pepPort) {
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
	public StatusType deployMechanismPmp(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pmp: deployMechPmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism1Pmp(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pmp: revokeMech1Pmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism2Pmp(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pmp: revokeMech2Pmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType deployPolicyPmp(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pmp: deployPolicyPmp");
		return StatusType.ERROR;
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pmp: listmechPmp");
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("test");
		m.put("mystring", l);
		return m;
	}

}
