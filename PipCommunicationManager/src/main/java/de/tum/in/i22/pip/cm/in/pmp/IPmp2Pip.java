package de.tum.in.i22.pip.cm.in.pmp;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPmp2Pip {
	public IStatus initialRepresentation(IContainer container, IData data);
}
