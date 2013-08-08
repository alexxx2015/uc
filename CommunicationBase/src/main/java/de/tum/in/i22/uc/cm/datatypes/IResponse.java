package de.tum.in.i22.uc.cm.datatypes;

import java.util.List;

import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public interface IResponse {
	public EStatus getAuthorizationAction();
	public List<IEvent> getExecuteActions();
	public IEvent getModifiedEvent();
}
