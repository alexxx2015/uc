package de.tum.in.i22.uc.cm.in.thrift;

import java.io.EOFException;
import java.io.IOException;
import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.i22.in.uc.cm.thrift.Container;
import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;



public class TAny2PipHandler extends ClientConnectionHandler implements TAny2Pip.Iface, IForwarder {

	private final int _port;

	public TAny2PipHandler(int pepPort) {
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
	public StatusType initialRepresentation(Container container, Data data)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAllData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StatusType notifyActualEvent(Event event) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusType notifyDataTransfer(Name containerName, Set<Data> data)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(Event event,
			String predicate) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Container> getContainerForData(Data data) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusType startSimulation() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusType stopSimulation() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimulating() throws TException {
		// TODO Auto-generated method stub
		return false;
	}


}
