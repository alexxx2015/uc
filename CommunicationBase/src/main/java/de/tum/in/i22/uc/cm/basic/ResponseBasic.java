package de.tum.in.i22.uc.cm.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;

public class ResponseBasic implements IResponse {
	private IStatus _authorizationAction = null;
	private List<IEvent> _executeActions = null;
	private IEvent _modifiedEvent = null;

	private final static IMessageFactory _factory = MessageFactoryCreator.createMessageFactory();

	public ResponseBasic(IStatus authorizationAction,
			List<IEvent> executeActions, IEvent modifiedEvent) {
		super();
		_authorizationAction = authorizationAction;
		_executeActions = executeActions;
		_modifiedEvent = modifiedEvent;
	}

	public ResponseBasic(GpResponse gpResponse) {
		if (gpResponse == null)
			return;

		if (gpResponse.hasAuthorizationAction()) {
			// Copy Authorization Action
			GpStatus gpAuthorizationAction = gpResponse.getAuthorizationAction();
			_authorizationAction = new StatusBasic(gpAuthorizationAction);
		}

		// Copy Execute Actions
		List<GpEvent> list = gpResponse.getExecuteActionList();
		if (list != null && list.size() > 0) {
			_executeActions = new ArrayList<IEvent>();
			for (GpEvent gpEvent:list) {
				IEvent event = _factory.createEvent(gpEvent);
				_executeActions.add(event);
			}
		}

		if (gpResponse.hasModifiedEvent()) {
			// Copy Modified Event
			GpEvent modifiedGpEvent = gpResponse.getModifiedEvent();
			_modifiedEvent = _factory.createEvent(modifiedGpEvent);
		}
	}

	@Override
	public IStatus getAuthorizationAction() {
		return _authorizationAction;
	}

	@Override
	public List<IEvent> getExecuteActions() {
		return _executeActions;
	}

	@Override
	public IEvent getModifiedEvent() {
		return _modifiedEvent;
	}

	/**
	 *
	 * @param e
	 * @return Google Protocol Buffer object corresponding to IResponse
	 */
	public static GpResponse createGpbResponse(IResponse response) {
		if (response == null)
			return null;

		PdpProtos.GpResponse.Builder gpResponse = PdpProtos.GpResponse.newBuilder();

		// Set authorization action
		if (response.getAuthorizationAction() != null) {
			GpStatus gpStatus = StatusBasic.createGpbStatus(response.getAuthorizationAction());
			gpResponse.setAuthorizationAction(gpStatus);
		}

		// Set modified event
		IEvent modifiedEvent = response.getModifiedEvent();
		if (modifiedEvent != null) {
			GpEvent modifiedGpEvent = EventBasic.createGpbEvent(modifiedEvent);
			gpResponse.setModifiedEvent(modifiedGpEvent);
		}

		// Set execute actions
		List<IEvent> executeActions = response.getExecuteActions();
		if (executeActions != null && !executeActions.isEmpty()) {
			for (IEvent executedAction:executeActions) {
				GpEvent executeGpAction = EventBasic.createGpbEvent(executedAction);
				gpResponse.addExecuteAction(executeGpAction);
			}
		}
		return gpResponse.build();
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
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
