package de.tum.in.i22.uc.thrift.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TChecksum;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TStatus;

/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PipThriftServer extends ThriftServerHandler implements
TAny2Pip.Iface {
	private static Logger _logger = LoggerFactory
			.getLogger(TAny2PipThriftServer.class);

	private final IAny2Pip _handler;

	TAny2PipThriftServer(IAny2Pip handler) {
		_handler = handler;
	}

	@Override
	public TStatus initialRepresentation(TName containerName, Set<TData> data)
			throws TException {
		_logger.debug("TAny2Pip: initialRepresentation");
		IName name = ThriftConverter.fromThrift(containerName);
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		IStatus status = _handler.initialRepresentation(name, d);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public TData newInitialRepresentation(TName containerName)
			throws TException {
		_logger.debug("TAny2Pip: newInitialRepresentation");
		IName name = ThriftConverter.fromThrift(containerName);
		IData _data = _handler.newInitialRepresentation(name);
		return ThriftConverter.toThrift(_data);
	}

	@Override
	public TStatus update(TEvent event) throws TException {
		_logger.debug("TAny2Pip: notifyActualEvent");
		IEvent ev = ThriftConverter.fromThrift(event);
		IStatus status = _handler.update(ev);
		return ThriftConverter.toThrift(status);
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event,
			String predicate) throws TException {
		_logger.debug("TAny2Pip: evaluatePredicateSimulatingNextState");
		IEvent ev = ThriftConverter.fromThrift(event);
		return _handler.evaluatePredicateSimulatingNextState(ev,
				predicate);
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		_logger.debug("TAny2Pip: evaluatePredicateCurrentState");
		return _handler.evaluatePredicateCurrentState(predicate);
	}

	@Override
	public Set<TContainer> getContainerForData(TData data) throws TException {
		_logger.debug("TAny2Pip: getContainerforData");

		IData d = ThriftConverter.fromThrift(data);
		Set<IContainer> result = _handler.getContainersForData(d);

		return ThriftConverter.toThriftContainerSet(result);
	}

	@Override
	public Set<TData> getDataInContainer(TName containerName) throws TException {
		_logger.debug("TAny2Pip: getDataInContainer");

		IName cn = ThriftConverter.fromThrift(containerName);
		Set<IData> result = _handler.getDataInContainer(cn);
		return ThriftConverter.toThriftDataSet(result);
	}

	@Override
	public TStatus startSimulation() throws TException {
		_logger.debug("TAny2Pip: startsimulation");
		IStatus result = _handler.startSimulation();
		return ThriftConverter.toThrift(result);
	}

	@Override
	public TStatus stopSimulation() throws TException {
		_logger.debug("TAny2Pip: stopSimulation");
		IStatus status = _handler.stopSimulation();
		return ThriftConverter.toThrift(status);
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.debug("TAny2Pip: isSimulating");
		return _handler.isSimulating();
	}

	@Override
	public TData newStructuredData(Map<String, Set<TData>> structure)
			throws TException {
		_logger.debug("TAny2Pip: newStructuredData");
		if (structure == null)
			throw new TException("structure==null");
		Map<String, Set<IData>> map = new HashMap<String, Set<IData>>();
		for (String s : structure.keySet()) {
			map.put(s, ThriftConverter.fromThriftDataSet(structure.get(s)));
		}
		return ThriftConverter.toThrift(_handler.newStructuredData(map));
	}

	@Override
	public Map<String, Set<TData>> getStructureOf(TData data) throws TException {
		_logger.debug("TAny2Pip: getStructureOf");
		if (data == null)
			throw new TException("TData==null");
		Map<String, Set<IData>> map = _handler
				.getStructureOf(ThriftConverter.fromThrift(data));
		Map<String, Set<TData>> res = new HashMap<String, Set<TData>>();
		for (String s : map.keySet()) {
			res.put(s, ThriftConverter.toThriftDataSet(map.get(s)));
		}
		return res;
	}

	@Override
	public Set<TData> flattenStructure(TData data) throws TException {
		_logger.debug("TAny2Pip: flattenDtructure");
		return ThriftConverter.toThriftDataSet(_handler
				.flattenStructure(ThriftConverter.fromThrift(data)));
	}

	@Override
	public TData getDataFromId(String id) throws TException {
		_logger.debug("TAny2Pip: getDataFromID");
		return ThriftConverter.toThrift(_handler.getDataFromId(id));
	}

	@Override
	public TStatus addJPIPListener(String ip, int port, String id, String filter)
			throws TException {
		_logger.debug("TAny2Pip: addJPIPListener");
		return ThriftConverter.toThrift(_handler.addJPIPListener(ip,port,id, filter));
	}

	@Override
	public TStatus setUpdateFrequency(int msec, String id) throws TException {
		_logger.debug("TAny2Pip: setUpdateFrequence");
		return ThriftConverter.toThrift(_handler.setUpdateFrequency(msec,id));
	}

	@Override
	public boolean deleteStructure(TData d) throws TException {
		_logger.debug("TAny2Pip: deleteStructure");
		return _handler
				.deleteStructure(ThriftConverter.fromThrift(d));
	}

	@Override
	public boolean deleteChecksum(TData d) throws TException {
		_logger.debug("TAny2Pip: deleteChecksum");
		return _handler
				.deleteChecksum(ThriftConverter.fromThrift(d));
	}

	@Override
	public TChecksum getChecksumOf(TData data) throws TException {
		_logger.debug("TAny2Pip: getChecksumOf");
		return ThriftConverter.toThrift(_handler.getChecksumOf(ThriftConverter.fromThrift(data)));
	}

	@Override
	public boolean newChecksum(TData data, TChecksum checksum, boolean overwrite) throws TException {
		_logger.debug("TAny2Pip: newChecksum");
		return _handler
				.newChecksum(ThriftConverter.fromThrift(data),ThriftConverter.fromThrift(checksum),overwrite);
	}

	@Override
	public Map<String, Set<Map<String, String>>> filterModel(Map<String, String> params) throws TException {
	    return _handler.filterModel(params);
	}
}
