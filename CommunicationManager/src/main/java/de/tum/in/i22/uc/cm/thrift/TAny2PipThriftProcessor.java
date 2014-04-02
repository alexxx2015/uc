package de.tum.in.i22.uc.cm.thrift;

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
import de.tum.in.i22.uc.pip.requests.EvaluatePredicateCurrentStatePipRequest;
import de.tum.in.i22.uc.pip.requests.EvaluatePredicateSimulatingNextStatePipRequest;
import de.tum.in.i22.uc.pip.requests.GetContainersForDataPipRequest;
import de.tum.in.i22.uc.pip.requests.GetDataInContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllContainersPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllData;
import de.tum.in.i22.uc.pip.requests.HasAnyContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAnyDataPipRequest;
import de.tum.in.i22.uc.pip.requests.InitialRepresentationPipRequest;
import de.tum.in.i22.uc.pip.requests.IsSimulatingPipRequest;
import de.tum.in.i22.uc.pip.requests.NotifyActualEventPipRequest;
import de.tum.in.i22.uc.pip.requests.StartSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.StopSimulationPipRequest;
import de.tum.in.i22.uc.thrift.ThriftTypes;


public class TAny2PipThriftProcessor extends ThriftServerHandler implements TAny2Pip.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PipThriftProcessor.class);

	@Override
	public StatusType initialRepresentation(Name containerName, Set<Data> data) throws TException {
		_logger.debug("TAny2Pip: initialRepresentation");

		InitialRepresentationPipRequest request = new InitialRepresentationPipRequest(
				ThriftTypes.fromThrift(containerName),
				ThriftTypes.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThrift(waitForResponse(request));
	}

	@Override
	public boolean hasAllData(Set<Data> data) throws TException {
		_logger.debug("TAny2Pip: hasAllData");

		HasAllData request = new HasAllData(ThriftTypes.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyData(Set<Data> data) throws TException {
		_logger.debug("TAny2Pip: hasAnyData");

		HasAnyDataPipRequest request = new HasAnyDataPipRequest(ThriftTypes.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllContainers(Set<Container> container) throws TException {
		_logger.debug("TAny2Pip: hasAllContainers");

		HasAllContainersPipRequest request =
				new HasAllContainersPipRequest(ThriftTypes.fromThriftContainerSet(container));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyContainer(Set<Container> container) throws TException {
		_logger.debug("TAny2Pip: hasAnyContainer");

		HasAnyContainerPipRequest request = new HasAnyContainerPipRequest(ThriftTypes.fromThriftContainerSet(container));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public StatusType notifyActualEvent(Event event) throws TException {
		_logger.debug("TAny2Pip: notifyActualEvent");

		NotifyActualEventPipRequest request = new NotifyActualEventPipRequest(ThriftTypes.fromThrift(event));
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThrift(waitForResponse(request));
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(Event event, String predicate) throws TException {
		_logger.debug("TAny2Pip: evaluatePredicateSimulatingNextState");

		EvaluatePredicateSimulatingNextStatePipRequest request =
				new EvaluatePredicateSimulatingNextStatePipRequest(ThriftTypes.fromThrift(event), predicate);
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate) throws TException {
		_logger.debug("TAny2Pip: evaluatePredicateCurrentState");

		EvaluatePredicateCurrentStatePipRequest request = new EvaluatePredicateCurrentStatePipRequest(predicate);
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<Container> getContainerForData(Data data) throws TException {
		_logger.debug("TAny2Pip: getContainerforData");

		GetContainersForDataPipRequest request = new GetContainersForDataPipRequest(ThriftTypes.fromThrift(data));
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThriftContainerSet(waitForResponse(request));
	}

	@Override
	public Set<Data> getDataInContainer(Container container) throws TException {
		_logger.debug("TAny2Pip: getDataInContainer");

		GetDataInContainerPipRequest request = new GetDataInContainerPipRequest(ThriftTypes.fromThrift(container));
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThriftDataSet(waitForResponse(request));
	}

	@Override
	public StatusType startSimulation() throws TException {
		_logger.debug("TAny2Pip: startsimulation");

		StartSimulationPipRequest request = new StartSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThrift(waitForResponse(request));
	}

	@Override
	public StatusType stopSimulation() throws TException {
		_logger.debug("TAny2Pip: stopSimulation");

		StopSimulationPipRequest request = new StopSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftTypes.toThrift(waitForResponse(request));
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.debug("TAny2Pip: isSimulating");

		IsSimulatingPipRequest request = new IsSimulatingPipRequest();
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}
}
