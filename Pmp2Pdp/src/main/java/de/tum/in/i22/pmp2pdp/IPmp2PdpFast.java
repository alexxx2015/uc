package de.tum.in.i22.pmp2pdp;

import de.tum.in.i22.pdp.cm.in.IPmp2Pdp;

public interface IPmp2PdpFast extends IPmp2Pdp {
	public void connect() throws Exception;
	public void disconnect();
}
