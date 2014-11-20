package de.tum.in.i22.uc.cm.datatypes.interfaces;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;

public interface IResponse {
	public IStatus getAuthorizationAction();
	public Collection<IEvent> getExecuteActions();
	public IEvent getModifiedEvent();
	public boolean isAuthorizationAction(EStatus status);
}
