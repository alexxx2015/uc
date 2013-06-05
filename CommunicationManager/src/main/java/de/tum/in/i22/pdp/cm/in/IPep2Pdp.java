package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IResponse;

public interface IPep2Pdp {
	public IResponse notifyEvent(IEvent event);
}
