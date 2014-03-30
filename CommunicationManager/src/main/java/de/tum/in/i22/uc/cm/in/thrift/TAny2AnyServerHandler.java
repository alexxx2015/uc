package de.tum.in.i22.uc.cm.in.thrift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.Container;
import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.Pxp;
import de.tum.i22.in.uc.cm.thrift.Response;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Any;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;

public class TAny2AnyServerHandler implements TAny2Any.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2AnyServerHandler.class);

	@Override
	public Response notifyEventSync(Event e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyEvent");
		return null;
	}

	@Override
	public boolean registerPxp(Pxp pxp) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: registerPxp");
		return false;
	}

	@Override
	public StatusType deployMechanism(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deploymech");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism1(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokemech1");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism2(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokemech2");
		return StatusType.ERROR;
	}

	@Override
	public StatusType deployPolicy(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployPolicy");
		return StatusType.ERROR;
	}

	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: listMech");
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("test");
		m.put("mystring", l);
		return m;
	}

	@Override
	public StatusType initialRepresentation(Container container, Data data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: initrep");
		return StatusType.ERROR;
	}

	@Override
	public boolean hasAllData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllData");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<Data> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnyData");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllContainers");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<Container> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnycontainers");
		return false;
	}

	@Override
	public StatusType notifyActualEvent(Event event) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyActualEvent");
		return StatusType.ERROR;
	}

	@Override
	public StatusType notifyDataTransfer(Name containerName, Set<Data> data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyDatatransfer");
		return StatusType.ERROR;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(Event event,
			String predicate) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: evaluatePredicateSimulatingNextState");
		return false;
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: evaluatePredicateCurrentState");
		return false;
	}

	@Override
	public Set<Container> getContainerForData(Data data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getContainerforData");
		return new HashSet<Container>();
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getDataInContainer");
		return new HashSet<Data>();
	}

	@Override
	public StatusType startSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: startsimulation");
		return StatusType.ERROR;
	}

	@Override
	public StatusType stopSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: stopSimulation");
		return StatusType.ERROR;
	}

	@Override
	public boolean isSimulating() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: isSimulating");
		return false;
	}

	@Override
	public StatusType deployMechanismPmp(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployMechPmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism1Pmp(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokeMech1Pmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism2Pmp(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokeMech2Pmp");
		return StatusType.ERROR;
	}

	@Override
	public StatusType deployPolicyPmp(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployPolicyPmp");
		return StatusType.ERROR;
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: listmechPmp");
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("test");
		m.put("mystring", l);
		return m;
	}

	@Override
	public void notifyEventAsync(Event e) throws TException {
		// TODO Auto-generated method stub

	}

}
