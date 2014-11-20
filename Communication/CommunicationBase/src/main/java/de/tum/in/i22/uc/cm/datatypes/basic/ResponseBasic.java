package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class ResponseBasic implements IResponse {
	private final IStatus _authorizationAction;
	private final Collection<IEvent> _executeActions;
	private final IEvent _modifiedEvent;

	public ResponseBasic(IStatus authorizationAction) {
		this(authorizationAction, Collections.emptyList(), null);
	}

	public ResponseBasic(IStatus authorizationAction, Collection<IEvent> executeActions, IEvent modifiedEvent) {
		_authorizationAction = authorizationAction;
		_executeActions = executeActions;
		_modifiedEvent = modifiedEvent;
	}


	@Override
	public IStatus getAuthorizationAction() {
		return _authorizationAction;
	}

	@Override
	public Collection<IEvent> getExecuteActions() {
		if (_executeActions == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(_executeActions);
	}

	@Override
	public IEvent getModifiedEvent() {
		return _modifiedEvent;
	}

	@Override
	public boolean isAuthorizationAction(EStatus status) {
		return _authorizationAction == null ? false : _authorizationAction.isStatus(status);
	}


	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_authorizationAction", _authorizationAction)
				.add("_executeAction", _executeActions)
				.add("_modifiedEvent", _modifiedEvent)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof ResponseBasic) {
			ResponseBasic o = (ResponseBasic)obj;
			isEqual = Objects.equals(_authorizationAction, o._authorizationAction)
					&& Objects.equals(_executeActions, o._executeActions)
					&& Objects.equals(_modifiedEvent, o._modifiedEvent);
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_authorizationAction, _executeActions, _modifiedEvent);
	}
}
