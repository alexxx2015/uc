package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPip2JPip;


public abstract class Pip2JPipClient extends GenericX2YClient implements IPip2JPip {
	protected Pip2JPipClient(Connector<?> connector) {
		super(connector);
	}
}
