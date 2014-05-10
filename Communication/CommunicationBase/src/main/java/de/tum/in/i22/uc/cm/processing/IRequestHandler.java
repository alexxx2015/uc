package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;


public interface IRequestHandler extends IAny2Pdp, IAny2Pip, IAny2Pmp {
	public String getIfModel();
	public void reset();
	public void stop();
}
