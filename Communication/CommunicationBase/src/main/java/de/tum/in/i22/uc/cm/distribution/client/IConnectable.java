package de.tum.in.i22.uc.cm.distribution.client;

import java.io.IOException;

/**
 * An interface for classes that can be connected to some remote point.
 *
 * @author Florian Kelbert
 *
 */
public interface IConnectable {
	/**
	 * Connects this object to the remote point
	 *
	 * @throws IOException if the connection cannot be established
	 */
	public void connect() throws IOException;

	/**
	 * Disconnects.
	 */
	public void disconnect();
}
