package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pxp;


public abstract class Any2PxpClient extends GenericX2YClient implements IAny2Pxp {
	protected Any2PxpClient(Connector<?> connector) {
		super(connector);
	}
}
