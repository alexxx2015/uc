package de.tum.in.i22.uc.cm.in.thrift;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.Container;
import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;


public class TAny2PipServerNull implements TAny2Pip.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PipServerNull.class);


	@Override
	public StatusType initialRepresentation(Container container, Data data)
			throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public boolean hasAllData(Set<Data> data) throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<Data> data) throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<Container> container) throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<Container> container) throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public StatusType notifyActualEvent(Event event) throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public StatusType notifyDataTransfer(Name containerName, Set<Data> data)
			throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(Event event,
			String predicate) throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

	@Override
	public Set<Container> getContainerForData(Data data) throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public StatusType startSimulation() throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public StatusType stopSimulation() throws TException {
		_logger.warn("No Pip thrift server running.");
		return null;
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.warn("No Pip thrift server running.");
		return false;
	}

}
