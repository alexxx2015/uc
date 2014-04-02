package de.tum.in.i22.uc.cm.thrift;

import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.Container;
import de.tum.i22.in.uc.thrift.types.Data;
import de.tum.i22.in.uc.thrift.types.Event;
import de.tum.i22.in.uc.thrift.types.Name;
import de.tum.i22.in.uc.thrift.types.StatusType;
import de.tum.i22.in.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.pip.requests.GetContainersForDataPipRequest;
import de.tum.in.i22.uc.pip.requests.InitialRepresentationPipRequest;
import de.tum.in.i22.uc.pip.requests.IsSimulatingPipRequest;
import de.tum.in.i22.uc.pip.requests.NotifyActualEventPipRequest;
import de.tum.in.i22.uc.pip.requests.StartSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.StopSimulationPipRequest;
import de.tum.in.i22.uc.thrift.ThriftTypeConversion;


public class TAny2PipThriftProcessor extends ThriftServerHandler implements TAny2Pip.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PipThriftProcessor.class);

	@Override
	public StatusType initialRepresentation(Name containerName, Set<Data> data)
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

		NotifyActualEventPipRequest request = new NotifyActualEventPipRequest(ThriftTypeConversion.fromThrift(event));
		_requestHandler.addRequest(request, this);
		return ThriftTypeConversion.toThrift(waitForResponse(request));
	}

	@Override
	public StatusType notifyDataTransfer(Name containerName, Set<Data> data) throws TException {
		_logger.debug("TAny2Pip: notifyDatatransfer");

		InitialRepresentationPipRequest request = new InitialRepresentationPipRequest(
				ThriftTypeConversion.fromThrift(containerName),
				ThriftTypeConversion.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return ThriftTypeConversion.toThrift(waitForResponse(request));
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
		_logger.debug("TAny2Pip: getContainerforData");

		GetContainersForDataPipRequest request = new GetContainersForDataPipRequest(ThriftTypeConversion.fromThrift(data));
		_requestHandler.addRequest(request, this);
		return ThriftTypeConversion.toThriftContainerSet(waitForResponse(request));
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pip: getDataInContainer");
		return new HashSet<Data>();
	}

	@Override
	public StatusType startSimulation() throws TException {
		_logger.debug("TAny2Pip: startsimulation");

		StartSimulationPipRequest request = new StartSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftTypeConversion.toThrift(waitForResponse(request));
	}

	@Override
	public StatusType stopSimulation() throws TException {
		_logger.debug("TAny2Pip: stopSimulation");

		StopSimulationPipRequest request = new StopSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftTypeConversion.toThrift(waitForResponse(request));
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.debug("TAny2Pip: isSimulating");

		IsSimulatingPipRequest request = new IsSimulatingPipRequest();
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}
}
