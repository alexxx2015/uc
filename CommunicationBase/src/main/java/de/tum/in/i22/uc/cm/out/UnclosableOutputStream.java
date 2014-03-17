package de.tum.in.i22.uc.cm.out;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Returns an unclosable view on an OutputStream.
 *
 * @author Florian Kelbert
 *
 */
public class UnclosableOutputStream extends FilterOutputStream {

	public UnclosableOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}
}
