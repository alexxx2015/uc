package de.tum.in.i22.pdp.core;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

public interface IPep2Pdp {
	public IResponse notifyEvent(IEvent event);
}
