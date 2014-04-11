package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp;


public abstract class Pdp2PxpClient extends GenericX2YClient implements IPdp2Pxp {
	protected Pdp2PxpClient(Connector<?> connector) {
		super(connector);
	}
}
