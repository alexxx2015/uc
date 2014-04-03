package de.tum.in.i22.uc.cm.thrift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Any;
import de.tum.i22.in.uc.thrift.types.TContainer;
import de.tum.i22.in.uc.thrift.types.TData;
import de.tum.i22.in.uc.thrift.types.TEvent;
import de.tum.i22.in.uc.thrift.types.TName;
import de.tum.i22.in.uc.thrift.types.TPxpSpec;
import de.tum.i22.in.uc.thrift.types.TResponse;
import de.tum.i22.in.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.server.ThriftServerHandler;


public class TAny2AnyThriftProcessor extends ThriftServerHandler implements TAny2Any.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2AnyThriftProcessor.class);

	@Override
	public TResponse notifyEventSync(TEvent e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyEventSync");
		return new TResponse(TStatus.ERROR);
	}

	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: registerPxp");
		return false;
	}

	@Override
	public TStatus deployMechanism(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deploymech");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism1(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokemech1");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism2(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokemech2");
		return TStatus.ERROR;
	}

	@Override
	public TStatus deployPolicy(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployPolicy");
		return TStatus.ERROR;
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
	public TStatus initialRepresentation(TContainer container, TData data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: initrep");
		return TStatus.ERROR;
	}

	@Override
	public boolean hasAllData(Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllData");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnyData");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<TContainer> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllContainers");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<TContainer> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnycontainers");
		return false;
	}

	@Override
	public TStatus notifyActualEvent(TEvent event) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyActualEvent");
		return TStatus.ERROR;
	}

	@Override
	public TStatus notifyDataTransfer(TName containerName, Set<TData> data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyDatatransfer");
		return TStatus.ERROR;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event,
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
	public Set<TContainer> getContainerForData(TData data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getContainerforData");
		return new HashSet<TContainer>();
	}

	@Override
	public Set<TData> getDataInContainer(TContainer container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getDataInContainer");
		return new HashSet<TData>();
	}

	@Override
	public TStatus startSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: startsimulation");
		return TStatus.ERROR;
	}

	@Override
	public TStatus stopSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: stopSimulation");
		return TStatus.ERROR;
	}

	@Override
	public boolean isSimulating() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: isSimulating");
		return false;
	}

	@Override
	public TStatus deployMechanismPmp(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployMechPmp");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism1Pmp(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokeMech1Pmp");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism2Pmp(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: revokeMech2Pmp");
		return TStatus.ERROR;
	}

	@Override
	public TStatus deployPolicyPmp(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: deployPolicyPmp");
		return TStatus.ERROR;
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
	public void notifyEventAsync(TEvent e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyEventAsync");

	}

}
