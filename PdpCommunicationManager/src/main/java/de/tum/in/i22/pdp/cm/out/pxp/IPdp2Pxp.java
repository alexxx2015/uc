package de.tum.in.i22.pdp.cm.out.pxp;

import de.tum.in.i22.cm.pdp.internal.ExecuteAction;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public interface IPdp2Pxp {
	
	public void sendExecAction2Pxp(IPxpSpec pxpSpec, ExecuteAction execAction);

}
