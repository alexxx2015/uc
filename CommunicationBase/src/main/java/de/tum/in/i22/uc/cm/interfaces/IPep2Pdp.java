package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.out.IConnection;

public interface IPep2Pdp extends IConnection {
	public Object notifyEvent(IEvent event);


}