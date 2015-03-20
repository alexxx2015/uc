package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IDmp2Dmp;

public abstract class Dmp2DmpClient extends GenericX2YClient implements IDmp2Dmp {
	protected Dmp2DmpClient(Connector<?> connector) {
		super(connector);
	}
}
