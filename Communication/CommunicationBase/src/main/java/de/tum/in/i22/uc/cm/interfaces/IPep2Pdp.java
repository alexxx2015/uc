package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

/**
 * Interface defining methods a PEP can invoke on a PDP.
 * @author Kelbert & Lovat
 *
 */
public interface IPep2Pdp {
	public void notifyEventAsync(IEvent event);
	public IResponse notifyEventSync(IEvent event);
}
