package de.tum.in.i22.uc.cm.in.thrift;

import java.util.HashSet;
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
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.thrift.ThriftTypeConversion;


public class TAny2PipServerHandler implements TAny2Pip.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PipServerHandler.class);

	private final IAny2Pip _pip;

	public TAny2PipServerHandler(IAny2Pip pip) {
		_pip = pip;
	}

	@Override
	public StatusType initialRepresentation(Container container, Data data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: initrep");
		return StatusType.ERROR;
	}

	@Override
	public boolean hasAllData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: hasAllData");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: hasAnyData");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: hasAllContainers");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: hasAnycontainers");
		return false;
	}

	@Override
	public StatusType notifyActualEvent(Event event) throws TException {
		_logger.debug("TAny2Pip: notifyActualEvent");
		_pip.notifyActualEvent(ThriftTypeConversion.convert(event));
		return StatusType.ERROR;
	}

	@Override
	public StatusType notifyDataTransfer(Name containerName, Set<Data> data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: notifyDatatransfer");
		return StatusType.ERROR;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(Event event,
			String predicate) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: evaluatePredicateSimulatingNextState");
		return false;
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: evaluatePredicateCurrentState");
		return false;
	}

	@Override
	public Set<Container> getContainerForData(Data data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: getContainerforData");
		return new HashSet<Container>();
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: getDataInContainer");
		return new HashSet<Data>();
	}

	@Override
	public StatusType startSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: startsimulation");
		return StatusType.ERROR;
	}

	@Override
	public StatusType stopSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: stopSimulation");
		return StatusType.ERROR;
	}

	@Override
	public boolean isSimulating() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: isSimulating");
		return false;
	}

}
