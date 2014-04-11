package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPip2Pmp;



public abstract class Pip2PmpClient extends GenericX2YClient implements IPip2Pmp {
	protected Pip2PmpClient(Connector<?> connector) {
		super(connector);
	}
}
