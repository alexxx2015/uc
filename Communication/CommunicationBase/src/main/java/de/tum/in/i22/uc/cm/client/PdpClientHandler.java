package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.server.PdpProcessor;

/**
 * This class represents the client side of a remote {@link PdpProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PdpProcessor}.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PdpClientHandler extends PdpProcessor {

	public abstract void connect() throws Exception;

	public abstract void disconnect();
}
