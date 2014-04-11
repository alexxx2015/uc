package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp;



public abstract class Pmp2PmpClient extends GenericX2YClient implements IPmp2Pmp {
	protected Pmp2PmpClient(Connector<?> connector) {
		super(connector);
	}
}
