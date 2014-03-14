package de.tum.in.i22.uc.distribution;

import java.io.IOException;

abstract class Connection {
	abstract public boolean isConnected();
	abstract void connect() throws IOException;
	abstract void disconnect() throws IOException;
	abstract public UnclosableInputStream getInputStream() throws IOException;
	abstract public UnclosableOutputStream getOutputStream() throws IOException;

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
