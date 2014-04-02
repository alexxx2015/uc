package de.tum.in.i22.uc.cm.client;

/**
 * An interface for classes that can be connected to some remote point.
 *
 * @author Florian Kelbert
 *
 * @param <HandleType> the type of the handle for the connection
 */
public interface IConnectable<HandleType> {
	/**
	 * Connects this object to the remote point and
	 * returns a handle of type <HandleType> that refers
	 * to the connection that has been established.
	 *
	 * @return a handle for the connection
	 * @throws Exception if the connection cannot be established
	 */
	public HandleType connect() throws Exception;

	/**
	 * Disconnects.
	 */
	public void disconnect();
}
