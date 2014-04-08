package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PDP can invoke on a PXP.
 * @author Kelbert & Lovat
 *
 */
public interface IPdp2Pxp {
	public IStatus executeSync(List<IEvent> event);
	public void executeAsync(List<IEvent> event);
}
