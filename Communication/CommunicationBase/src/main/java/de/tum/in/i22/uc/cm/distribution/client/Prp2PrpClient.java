package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;

public abstract class Prp2PrpClient extends GenericX2YClient implements IAny2Prp{
	protected Prp2PrpClient(Connector<?> connector) {
		super(connector);
	}

}
