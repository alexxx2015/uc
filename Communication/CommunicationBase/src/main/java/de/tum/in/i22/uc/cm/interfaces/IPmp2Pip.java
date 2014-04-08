package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods a PMP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
public interface IPmp2Pip {
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}
