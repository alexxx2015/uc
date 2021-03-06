package de.tum.in.i22.uc.pdp.distribution;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pdp.core.operators.Operator;

/**
 * This class wraps an IResponse and adds additional functions
 * needed for distribution.
 *
 * @author Florian Kelbert
 *
 */
public class DistributedPdpResponse implements IResponse {

	private final IResponse _response;
	private final List<Operator> _changedOperators;

	/**
	 * Creates a new {@link DistributedPdpResponse} instance by wrapping the specified
	 * {@link IResponse} and by attaching the specified additional parameters to that object.
	 *
	 * @param response
	 * @param _stateBasedOperatorTrue
	 * @param operatorStateChanges
	 */
	public DistributedPdpResponse(IResponse response, List<Operator> changedOperators) {
		_response = response;
		_changedOperators = new LinkedList<>(changedOperators);
	}

	public List<Operator> getChangedOperators() {
		return _changedOperators;
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
	public Collection<IEvent> getExecuteActions() {
		return _response.getExecuteActions();
	}

	@Override
	public IEvent getModifiedEvent() {
		return _response.getModifiedEvent();
	}

	@Override
	public boolean isAuthorizationAction(EStatus status) {
		return _response.isAuthorizationAction(status);
	}
}
