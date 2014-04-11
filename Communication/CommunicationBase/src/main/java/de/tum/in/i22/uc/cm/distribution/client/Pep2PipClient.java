package de.tum.in.i22.uc.cm.distribution.client;

import de.tum.in.i22.uc.cm.interfaces.IPep2Pip;


public abstract class Pep2PipClient extends GenericX2YClient implements IPep2Pip {
	protected Pep2PipClient(Connector<?> connector) {
		super(connector);
	}
}
