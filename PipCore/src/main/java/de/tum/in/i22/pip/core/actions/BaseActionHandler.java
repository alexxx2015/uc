package de.tum.in.i22.pip.core.actions;

import java.util.Map;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public abstract class BaseActionHandler implements IActionHandler {
	private IEvent _event;
	public BaseActionHandler() {
		super();
	}
	
	public IEvent getEvent() {
		return _event;
	}
	
	@Override
	public void setEvent(IEvent event) {
		_event = event;
	}
	
	protected String getParameterValue(String key) throws ParameterNotFoundException {
		Map<String, String> parameters = getEvent().getParameters();
		if (parameters != null && parameters.containsKey(key)) {
			return parameters.get(key);
		} else {
			throw new ParameterNotFoundException(key);
		}
	}
	
	protected InformationFlowModel getInformationFlowModel() {
		return InformationFlowModel.getInstance();
	}
}
