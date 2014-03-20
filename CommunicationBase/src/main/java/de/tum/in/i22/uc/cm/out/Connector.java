package de.tum.in.i22.uc.cm.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class Connector {
	protected static final Logger _logger = Logger.getLogger(Connector.class);

	private OutputStream _outputStream;
 	private InputStream _inputStream;

	UnclosableOutputStream getOutputStream() {
		return new UnclosableOutputStream(_outputStream);
	}

	UnclosableInputStream getInputStream() {
		return new UnclosableInputStream(_inputStream);
	}

	protected void setOutputStream(OutputStream out) {
		_outputStream = new BufferedOutputStream(out);
	}

	protected void setInputStream(InputStream in) {
		_inputStream = new BufferedInputStream(in);
	}

	protected void close() throws IOException {
		_outputStream.close();
		_inputStream.close();
	}

	abstract void connect() throws IOException;
	abstract void disconnect();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
