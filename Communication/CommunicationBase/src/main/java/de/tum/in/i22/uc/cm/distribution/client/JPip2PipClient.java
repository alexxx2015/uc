package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IJPip2Pip;


public abstract class JPip2PipClient extends GenericX2YClient implements IJPip2Pip {
	protected JPip2PipClient(Connector<?> connector) {
		super(connector);
	}
}
