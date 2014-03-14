package de.tum.in.i22.uc.distribution;

import java.io.IOException;

public abstract class Connection {
	abstract public boolean isConnected();
	abstract public void connect() throws IOException;
	abstract public void disconnect() throws IOException;
	abstract public UnclosableInputStream getInputStream() throws IOException;
	abstract public UnclosableOutputStream getOutputStream() throws IOException;

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
