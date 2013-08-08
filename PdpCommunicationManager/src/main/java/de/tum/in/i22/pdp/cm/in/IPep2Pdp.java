package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

public interface IPep2Pdp {
	public IResponse notifyEvent(IEvent event);
}
