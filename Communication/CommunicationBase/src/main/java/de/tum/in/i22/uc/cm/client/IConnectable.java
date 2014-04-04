package de.tum.in.i22.uc.cm.client;

/**
 * An interface for classes that can be connected to some remote point.
 *
 * @author Florian Kelbert
 *
 */
interface IConnectable {
	/**
	 * Connects this object to the remote point
	 *
	 * @throws Exception if the connection cannot be established
	 */
	public void connect() throws Exception;

	/**
	 * Disconnects.
	 */
	public void disconnect();
}
