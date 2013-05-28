package de.tum.in.i22.pdp.datatypes;

import java.util.List;

import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;

public interface IResponse {
	public EStatus getAuthorizationAction();
	public List<IEvent> getExecutedActions();
	public IEvent getModifiedEvent();
}
