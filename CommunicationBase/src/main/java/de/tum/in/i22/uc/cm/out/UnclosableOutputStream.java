package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Returns an unclosable view on an OutputStream.
 *
 * @author Florian Kelbert
 *
 */
public class UnclosableOutputStream extends OutputStream {
	private final OutputStream _stream;

	public UnclosableOutputStream(OutputStream stream) {
		_stream = stream;
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(int b) throws IOException {
		_stream.write(b);
	}
}
