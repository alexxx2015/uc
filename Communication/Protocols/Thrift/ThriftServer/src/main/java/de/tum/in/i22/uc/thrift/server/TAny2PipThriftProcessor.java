package de.tum.in.i22.uc.thrift.server;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.requests.pip.EvaluatePredicateCurrentStatePipRequest;
import de.tum.in.i22.uc.cm.requests.pip.EvaluatePredicateSimulatingNextStatePipRequest;
import de.tum.in.i22.uc.cm.requests.pip.GetContainersForDataPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.GetDataInContainerPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.HasAllContainersPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.HasAllData;
import de.tum.in.i22.uc.cm.requests.pip.HasAnyContainerPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.HasAnyDataPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.InitialRepresentationPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.IsSimulatingPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.StartSimulationPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.StopSimulationPipRequest;
import de.tum.in.i22.uc.cm.requests.pip.UpdatePipRequest;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftProcessorFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PipThriftProcessor extends ThriftServerHandler implements TAny2Pip.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PipThriftProcessor.class);

	private final IRequestHandler _requestHandler;

	TAny2PipThriftProcessor(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TStatus initialRepresentation(TName containerName, Set<TData> data) throws TException {
		_logger.debug("TAny2Pip: initialRepresentation");

		InitialRepresentationPipRequest request = new InitialRepresentationPipRequest(
				ThriftConverter.fromThrift(containerName),
				ThriftConverter.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public boolean hasAllData(Set<TData> data) throws TException {
		_logger.debug("TAny2Pip: hasAllData");

		HasAllData request = new HasAllData(ThriftConverter.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyData(Set<TData> data) throws TException {
		_logger.debug("TAny2Pip: hasAnyData");

		HasAnyDataPipRequest request = new HasAnyDataPipRequest(ThriftConverter.fromThriftDataSet(data));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllContainers(Set<TContainer> container) throws TException {
		_logger.debug("TAny2Pip: hasAllContainers");

		HasAllContainersPipRequest request =
				new HasAllContainersPipRequest(ThriftConverter.fromThriftContainerSet(container));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyContainer(Set<TContainer> container) throws TException {
		_logger.debug("TAny2Pip: hasAnyContainer");

		HasAnyContainerPipRequest request = new HasAnyContainerPipRequest(ThriftConverter.fromThriftContainerSet(container));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public TStatus update(TEvent event) throws TException {
		_logger.debug("TAny2Pip: notifyActualEvent");

		UpdatePipRequest request = new UpdatePipRequest(ThriftConverter.fromThrift(event));
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event, String predicate) throws TException {
		_logger.debug("TAny2Pip: evaluatePredicateSimulatingNextState");

		EvaluatePredicateSimulatingNextStatePipRequest request =
				new EvaluatePredicateSimulatingNextStatePipRequest(ThriftConverter.fromThrift(event), predicate);
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
	public Set<TContainer> getContainerForData(TData data) throws TException {
		_logger.debug("TAny2Pip: getContainerforData");

		GetContainersForDataPipRequest request = new GetContainersForDataPipRequest(ThriftConverter.fromThrift(data));
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThriftContainerSet(waitForResponse(request));
	}

	@Override
	public Set<TData> getDataInContainer(TContainer container) throws TException {
		_logger.debug("TAny2Pip: getDataInContainer");

		GetDataInContainerPipRequest request = new GetDataInContainerPipRequest(ThriftConverter.fromThrift(container));
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThriftDataSet(waitForResponse(request));
	}

	@Override
	public TStatus startSimulation() throws TException {
		_logger.debug("TAny2Pip: startsimulation");

		StartSimulationPipRequest request = new StartSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public TStatus stopSimulation() throws TException {
		_logger.debug("TAny2Pip: stopSimulation");

		StopSimulationPipRequest request = new StopSimulationPipRequest();
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public boolean isSimulating() throws TException {
		_logger.debug("TAny2Pip: isSimulating");

		IsSimulatingPipRequest request = new IsSimulatingPipRequest();
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}
}
