package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public interface IPep2Pdp {
	public EStatus notifyEvent(IEvent event);
}
