package de.tum.in.i22.uc.cm.out;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class Connector<Handle> {
	protected static final Logger _logger = LoggerFactory.getLogger(Connector.class);

	/**
	 * Connects the connector and returns a handle for communication.
	 * @return
	 * @throws Exception
	 */
	public abstract Handle connect() throws Exception;
	public abstract void disconnect();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
