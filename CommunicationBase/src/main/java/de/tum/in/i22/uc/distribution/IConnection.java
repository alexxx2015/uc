package de.tum.in.i22.uc.distribution;

import java.io.IOException;

public interface IConnection {
	boolean isConnected();
	void connect() throws IOException;
	void disconnect() throws IOException;
	UnclosableInputStream getInputStream() throws IOException;
	UnclosableOutputStream getOutputStream() throws IOException;
}
