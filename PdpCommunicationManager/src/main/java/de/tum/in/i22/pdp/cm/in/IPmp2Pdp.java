package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;

public interface IPmp2Pdp {
	public EStatus deployMechanism(IMechanism mechanism);
	public IMechanism exportMechanism(String par);
	public EStatus revokeMechanism(String par);
}
