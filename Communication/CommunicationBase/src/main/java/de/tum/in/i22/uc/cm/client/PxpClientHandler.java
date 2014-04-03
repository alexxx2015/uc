package de.tum.in.i22.uc.cm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Pxp;

/**
 * This class represents the client side of a remote {@link PxpProcessor}.
 *
 * This class operates on a {@link Connector} object that will be used to
 * connect to the remote {@link PxpProcessor}.
 *
 * @author Enrico Lovat
 *
 */
public abstract class PxpClientHandler implements IAny2Pxp {

	public abstract void connect() throws Exception;

	public abstract void disconnect();
}
