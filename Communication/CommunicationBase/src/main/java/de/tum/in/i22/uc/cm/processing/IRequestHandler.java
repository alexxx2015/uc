package de.tum.in.i22.uc.cm.processing;

import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;


public interface IRequestHandler extends IAny2Pdp, IAny2Pip, IAny2Pmp, IAny2Dmp, IAny2Prp {
	public String getIfModel();
	public boolean reset();
	public void stop();
}
