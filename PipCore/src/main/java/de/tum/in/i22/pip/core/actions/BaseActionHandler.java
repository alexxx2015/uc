package de.tum.in.i22.pip.core.actions;

import java.util.Map;

import de.tum.in.i22.pip.core.PipModel;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public abstract class BaseActionHandler implements IActionHandler {
	private IEvent _event;
	private PipModel _ifModel;
	public BaseActionHandler(IEvent event, PipModel ifModel) {
		super();
		_event = event;
		_ifModel = ifModel;
	}
	
	public IEvent getEvent() {
		return _event;
	}
	
	public PipModel getIfModel() {
		return _ifModel;
	}
	
	@Override
	public void setEvent(IEvent event) {
		_event = event;
	}
	
	protected String getParameterValue(String key) throws ParameterNotFoundException {
		Map<String, String> parameters = getEvent().getParameters();
		if (parameters.containsKey(key)) {
			return parameters.get(key);
		} else {
			throw new ParameterNotFoundException(key);
		}
	}
}
