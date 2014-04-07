package de.tum.in.i22.uc.thrift.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Enrico Lovat & Florian Kelbert
 *
 */
class TAny2AnyThriftServer extends ThriftServerHandler implements TAny2Any.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2AnyThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2AnyThriftServer(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TResponse notifyEventSync(TEvent e) throws TException {
		_logger.debug("TAny2Any: notifyEventSync");
		IEvent ev = ThriftConverter.fromThrift(e);
		IResponse r = _requestHandler.notifyEventSync(ev);
		return ThriftConverter.toThrift(r);
	}

	@Override
	public void notifyEventAsync(TEvent e) throws TException {
		//identical to sync version, but discards the response
		_logger.debug("TAny2Any: notifyEventAsync");
		IEvent ev = ThriftConverter.fromThrift(e);
		_requestHandler.notifyEventAsync(ev);
	}


	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		_logger.debug("TAny2Any: registerPxp");
		return _requestHandler.registerPxp(ThriftConverter.fromThrift(pxp));
	}

	@Override
	public TStatus revokePolicy(String policyName) throws TException {
		_logger.debug("TAny2Any: revokePolicy");
		IStatus status = _requestHandler.revokePolicy(policyName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus revokeMechanism(String policyName, String mechName) throws TException {
		_logger.debug("TAny2Any: revokeMechanism");
		IStatus status = _requestHandler.revokeMechanism(policyName, mechName);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyURI(String policyFilePath) throws TException {
		_logger.debug("TAny2Any: deployPolicy");
		IStatus status = _requestHandler.deployPolicyURI(policyFilePath);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus deployPolicyXML(String XMLPolicy) throws TException {
		_logger.debug("TAny2Any: deployPolicy");
		IStatus status = _requestHandler.deployPolicyXML(XMLPolicy);
		return ThriftConverter.toThrift(status);
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
	public TStatus initialRepresentation(TName container, Set<TData> data) throws TException {
		_logger.debug("TAny2Any: initrep");
		IName name = ThriftConverter.fromThrift(container);
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		IStatus status = _requestHandler.initialRepresentation(name, d);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public boolean hasAllData(Set<TData> data) throws TException {
		_logger.debug("TAny2Any: hasAllData");
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		return _requestHandler.hasAllData(d);
	}

	@Override
	public boolean hasAnyData(Set<TData> data) throws TException {
		_logger.debug("TAny2Any: hasAnyData");
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		return _requestHandler.hasAnyData(d);
	}

	@Override
	public boolean hasAllContainers(Set<TContainer> container) throws TException {
		_logger.debug("TAny2Any: hasAllContainers");
		Set<IContainer> c = ThriftConverter.fromThriftContainerSet(container);
		return _requestHandler.hasAllContainers(c);
	}

	@Override
	public boolean hasAnyContainer(Set<TContainer> container) throws TException {
		_logger.debug("TAny2Any: hasAnycontainers");
		Set<IContainer> c = ThriftConverter.fromThriftContainerSet(container);
		return _requestHandler.hasAnyContainer(c);
	}

	@Override
	public TStatus update(TEvent event) throws TException {
		_logger.debug("TAny2Any: notifyActualEvent");
		IEvent ev = ThriftConverter.fromThrift(event);
		IStatus status = _requestHandler.update(ev);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TStatus notifyDataTransfer(TName containerName, Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyDatatransfer");
		return TStatus.ERROR;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event, String predicate) throws TException {
		_logger.debug("TAny2Any: evaluatePredicateSimulatingNextState");
		IEvent ev = ThriftConverter.fromThrift(event);
		return _requestHandler.evaluatePredicateSimulatingNextState(ev, predicate);
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate) throws TException {
		_logger.debug("TAny2Any: evaluatePredicateCurrentState");
		return _requestHandler.evaluatePredicateCurrentState(predicate);
	}

	@Override
	public Set<TContainer> getContainerForData(TData data) throws TException {
		_logger.debug("TAny2Any: getContainerforData");

		IData d = ThriftConverter.fromThrift(data);
		Set<IContainer> result = _requestHandler.getContainersForData(d);

		return ThriftConverter.toThriftContainerSet(result);
	}

	@Override
	public Set<TData> getDataInContainer(TContainer container) throws TException {
		_logger.debug("TAny2Any: getDataInContainer");

		IContainer c = ThriftConverter.fromThrift(container);
		Set<IData> result = _requestHandler.getDataInContainer(c);
		return ThriftConverter.toThriftDataSet(result);
	}

	@Override
	public TStatus startSimulation() throws TException {
		_logger.debug("TAny2Any: startsimulation");
		IStatus result = _requestHandler.startSimulation();
		return ThriftConverter.toThrift(result);
	}

	@Override
	public TStatus stopSimulation() throws TException {
		_logger.debug("TAny2Any: stopSimulation");
		IStatus status = _requestHandler.stopSimulation();
		return ThriftConverter.toThrift(status);
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.debug("TAny2Any: isSimulating");
		return _requestHandler.isSimulating();
	}


}
