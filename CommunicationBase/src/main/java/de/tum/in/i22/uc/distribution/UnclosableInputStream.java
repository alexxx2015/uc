package de.tum.in.i22.uc.distribution;

import java.io.IOException;
import java.io.InputStream;

/**
 * Returns an unclosable view on an InputStream.
 *
 * @author Florian Kelbert
 *
 */
public class UnclosableInputStream extends InputStream {
	private final InputStream _stream;

	public UnclosableInputStream(InputStream stream) {
		_stream = stream;
	}

	@Override
	public int read() throws IOException {
		return _stream.read();
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}
}
