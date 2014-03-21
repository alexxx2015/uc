package de.tum.in.i22.uc.cm.datatypes;

import java.util.List;

public interface IObject {
	public IStatus getAuthorizationAction();
	public List<IEvent> getExecuteActions();
	public IEvent getModifiedEvent();
}
