package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.IConnection;

public interface IPmp2Pip extends IConnection {
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}
