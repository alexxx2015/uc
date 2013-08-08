package de.tum.in.i22.pip.cm.in.pmp;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public interface IPmp2Pip {
	public EStatus initialRepresentation(IContainer container, IData data);
}
