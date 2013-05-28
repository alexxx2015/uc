package de.tum.in.i22.pdp.datatypes;

public interface IMechanism {
	public String getName();
	public ICondition getCondition();
	public IResponse getResponse();
	public IHistory getState();
	public IEvent getTriggerEvent();
}
