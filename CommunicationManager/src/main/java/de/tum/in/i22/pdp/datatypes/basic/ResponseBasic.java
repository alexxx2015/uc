package de.tum.in.i22.pdp.datatypes.basic;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class ResponseBasic implements IResponse {
	private EStatus _authorizationAction = null;
	private List<IEvent> _executeActions = null;
	private IEvent _modifiedEvent = null;
	
	//TODO think if the factory is really necessary
	private final static IMessageFactory _factory = MessageFactory.getInstance();
	
	public ResponseBasic(EStatus authorizationAction,
			List<IEvent> executeActions, IEvent modifiedEvent) {
		super();
		_authorizationAction = authorizationAction;
		_executeActions = executeActions;
		_modifiedEvent = modifiedEvent;
	}

	public ResponseBasic(GpResponse gpResponse) {
		// Copy Authorization Action
		GpStatus gpAuthorizationAction = gpResponse.getAuthorizationAction();
		// Check for null, the constructor should not fail
		if (gpAuthorizationAction != null)
			_authorizationAction = gpAuthorizationAction.getValue();
		
		// Copy Execute Actions
		List<GpEvent> list = gpResponse.getExecuteActionList();
		if (list != null && list.size() > 0) {
			_executeActions = new ArrayList<IEvent>();
			for (GpEvent gpEvent:list) {
				IEvent event = _factory.createEvent(gpEvent);
				_executeActions.add(event);
			}
		}
		
		// Copy Modified Event
		GpEvent modifiedGpEvent = gpResponse.getModifiedEvent();
		if (modifiedGpEvent != null)
			_modifiedEvent = _factory.createEvent(modifiedGpEvent);
	}
	
	@Override
	public EStatus getAuthorizationAction() {
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
		PdpProtos.GpResponse.Builder gpResponse = PdpProtos.GpResponse.newBuilder();
		
		// Set authorization action
		GpStatus.Builder gpStatus = GpStatus.newBuilder();
		gpStatus.setValue(response.getAuthorizationAction());
		gpResponse.setAuthorizationAction(gpStatus.build());
		
		// Set modified event
		IEvent modifiedEvent = response.getModifiedEvent();
		GpEvent modifiedGpEvent = EventBasic.createGpbEvent(modifiedEvent);
		gpResponse.setModifiedEvent(modifiedGpEvent);
		
		// Set execute actions
		List<IEvent> executeActions = response.getExecuteActions();
		for (IEvent executedAction:executeActions) {
			GpEvent executeGpAction = EventBasic.createGpbEvent(executedAction);
			gpResponse.addExecuteAction(executeGpAction);
		}
		
		return gpResponse.build();
	}

	@Override
	public String toString() {
		return "ResponseBasic [_authorizationAction=" + _authorizationAction
				+ ", _executeActions=" + _executeActions + ", _modifiedEvent="
				+ _modifiedEvent + "]";
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			ResponseBasic o = (ResponseBasic)obj;
			isEqual = CompareUtil.areObjectsEqual(
						_authorizationAction, o.getAuthorizationAction())
					&& CompareUtil.areListsEqual(
							_executeActions, o.getExecuteActions())
					&& CompareUtil.areObjectsEqual(
							_modifiedEvent, o.getModifiedEvent());
		}
		return isEqual;
	}
}
