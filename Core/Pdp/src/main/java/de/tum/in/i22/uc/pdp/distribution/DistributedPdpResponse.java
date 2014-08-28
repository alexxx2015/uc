package de.tum.in.i22.uc.pdp.distribution;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pdp.core.condition.operators.OperatorState;

/**
 * This class wraps an IResponse and adds additional functions
 * needed for distribution.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPdpResponse implements IResponse {

	private final IResponse _response;
	private final Queue<OperatorState> _operatorStateChanges;

	/**
	 * Creates a new {@link DistributedPdpResponse} instance by wrapping the specified
	 * {@link IResponse} and by attaching the specified changed {@link OperatorState} to it.
	 * The rationale is that the provided {@link OperatorState}s have been changed during
	 * the assembly of the provided {@link IResponse}.
	 *
	 * @param response
	 * @param operatorStateChanges
	 */
	public DistributedPdpResponse(IResponse response, Queue<OperatorState> operatorStateChanges) {
		_response = response;
		_operatorStateChanges = new LinkedList<>(operatorStateChanges);
	}

	/**
	 * Returns the {@link OperatorState}s that have been changed during the
	 * assembly of the wrapped {@link IResponse} object.
	 *
	 * @return
	 */
	public Queue<OperatorState> getOperatorStateChanges() {
		return _operatorStateChanges;
	}

	/**
	 * Unwrap the internally wrapped {@link IResponse} object and return it.
	 * Functionality provided by {@link DistributedPdpResponse} is
	 * (generally) no longer available on the returned {@link IResponse} object.
	 *
	 * @return the internally wrapped {@link IResponse} object.
	 */
	public IResponse unwrap() {
		return _response;
	}

	@Override
	public IStatus getAuthorizationAction() {
		return _response.getAuthorizationAction();
	}

	@Override
	public List<IEvent> getExecuteActions() {
		return _response.getExecuteActions();
	}

	@Override
	public IEvent getModifiedEvent() {
		return _response.getModifiedEvent();
	}
}
