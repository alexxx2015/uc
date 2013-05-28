package de.tum.in.i22.pdp;

import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;


public interface IResponder {
	public void forwardResponse(EStatus status);
}
