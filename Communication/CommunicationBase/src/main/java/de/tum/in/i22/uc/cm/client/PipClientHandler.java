package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.server.PipProcessor;

/**
 * This class represents the client side of a remote {@link PipProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PipProcessor}.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PipClientHandler extends PipProcessor {

	public abstract void connect() throws Exception;

	public abstract void disconnect();
}
