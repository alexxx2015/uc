package de.tum.in.i22.uc.cm.datatypes.interfaces;


public interface IDecision {

	public abstract void processMechanism(IMechanism mech, IEvent curEvent);

	public abstract IResponse toResponse();

}