package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IDistr2Distr;

public abstract class Distr2DistrClient extends GenericX2YClient implements IDistr2Distr {
	protected Distr2DistrClient(Connector<?> connector) {
		super(connector);
	}
}
