package de.tum.in.i22.uc.pdp.distribution;

import java.util.LinkedList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pdp.core.condition.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.condition.operators.OperatorState;
import de.tum.in.i22.uc.pdp.core.condition.operators.StateBasedOperator;

/**
 * This class wraps an IResponse and adds additional functions
 * needed for distribution.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPdpResponse implements IResponse {

	private final IResponse _response;
//	private final Queue<IOperatorState> _operatorStateChanges;
	private final List<EventMatchOperator> _eventMatches;
	private final List<StateBasedOperator> _stateBasedOperatorTrue;

	/**
	 * Creates a new {@link DistributedPdpResponse} instance by wrapping the specified
	 * {@link IResponse} and by attaching the specified changed {@link OperatorState} to it.
	 * The rationale is that the provided {@link OperatorState}s have been changed during
	 * the assembly of the provided {@link IResponse}.
	 *
	 * @param response
	 * @param _stateBasedOperatorTrue
	 * @param operatorStateChanges
	 */
	public DistributedPdpResponse(IResponse response, List<EventMatchOperator> eventMatches, List<StateBasedOperator> stateBasedOperatorTrue) {
		_response = response;
//		_operatorStateChanges = new LinkedList<>(operatorStateChanges);

		/*
		 * TODO: Check whether creating a new list is actually necessary or whether we can reuse the passed list
		 */
		_eventMatches = new LinkedList<>(eventMatches);
		_stateBasedOperatorTrue = new LinkedList<>(stateBasedOperatorTrue);
	}

//	/**
//	 * Returns the {@link OperatorState}s that have been changed during the
//	 * assembly of the wrapped {@link IResponse} object.
//	 *
//	 * @return
//	 */
//	public Queue<IOperatorState> getOperatorStateChanges() {
//		return _operatorStateChanges;
//	}

	public List<EventMatchOperator> getEventMatches() {
		return _eventMatches;
	}

	public List<StateBasedOperator> getStateBasedOperatorTrue() {
		return _stateBasedOperatorTrue;
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
