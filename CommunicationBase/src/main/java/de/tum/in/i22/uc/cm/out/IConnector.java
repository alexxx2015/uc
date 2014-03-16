package de.tum.in.i22.uc.cm.out;

import java.io.IOException;

public interface IConnector {
	public void connect() throws IOException;
	public void disconnect();
}
