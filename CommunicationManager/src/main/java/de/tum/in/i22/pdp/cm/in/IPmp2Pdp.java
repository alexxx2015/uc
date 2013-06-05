package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public interface IPmp2Pdp {
	public EStatus deployMechanism(IMechanism mechanism);
	public EStatus exportMechanism(String par);
	public EStatus revokeMechanism(String par);
}
