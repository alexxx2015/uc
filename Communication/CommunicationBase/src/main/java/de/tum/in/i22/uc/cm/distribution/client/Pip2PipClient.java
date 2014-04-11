package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPip2Pip;


public abstract class Pip2PipClient extends GenericX2YClient implements IPip2Pip {
	protected Pip2PipClient(Connector<?> connector) {
		super(connector);
	}
}
