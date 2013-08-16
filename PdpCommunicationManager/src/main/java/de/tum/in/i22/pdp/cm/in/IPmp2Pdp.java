package de.tum.in.i22.pdp.cm.in;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IPmp2Pdp {
	public IStatus deployMechanism(IMechanism mechanism);
	public IMechanism exportMechanism(String par);
	public IStatus revokeMechanism(String par);
}
