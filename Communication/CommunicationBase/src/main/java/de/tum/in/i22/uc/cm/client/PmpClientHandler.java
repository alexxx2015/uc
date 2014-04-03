package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.server.PmpProcessor;

/**
 * This class represents the client side of a remote {@link PmpProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PmpProcessor}.
 *
 * @author Florian Kelbert
 *
 */
public abstract class PmpClientHandler extends PmpProcessor {

	public abstract void connect() throws Exception;

	public abstract void disconnect();
}
