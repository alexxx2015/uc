package de.tum.in.i22.uc.cm.distribution.client;

import java.io.IOException;

/**
 * Abstract class defining a connector that will operate on
 * a specific HandleType. Using this class as a superclass
 * will force subclasses to implement {@link Connector#hashCode()}
 * and {@link Connector#equals(Object)}, such that it is possible
 * to say whether two Connectors point to the same remote point.
 *
 * @author Florian Kelbert
 *
 */
public abstract class Connector<HandleType> {
	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	public abstract HandleType connect() throws IOException;

	public abstract void disconnect();
}
