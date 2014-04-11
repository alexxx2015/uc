package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp;

public abstract class Pxp2PdpClient extends GenericX2YClient implements IPxp2Pdp {
	protected Pxp2PdpClient(Connector<?> connector) {
		super(connector);
	}
}
