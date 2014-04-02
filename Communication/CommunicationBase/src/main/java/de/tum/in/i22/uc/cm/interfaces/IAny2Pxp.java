package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods to be invoked on a PXP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pxp {
	/*
	 * From PDP
	 */
	public IStatus execute(List<IEvent> event);
}
