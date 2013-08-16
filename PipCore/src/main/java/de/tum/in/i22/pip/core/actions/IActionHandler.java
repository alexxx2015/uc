package de.tum.in.i22.pip.core.actions;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public interface IActionHandler {
	public EStatus execute();

	public void setEvent(IEvent event);
}
