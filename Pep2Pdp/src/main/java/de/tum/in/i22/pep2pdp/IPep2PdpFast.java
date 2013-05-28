package de.tum.in.i22.pep2pdp;

import de.tum.in.i22.pdp.cm.in.IPep2Pdp;

public interface IPep2PdpFast extends IPep2Pdp {
	public void connect() throws Exception;
	public void disconnect();
}
