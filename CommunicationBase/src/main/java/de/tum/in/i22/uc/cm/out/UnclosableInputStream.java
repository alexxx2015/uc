package de.tum.in.i22.uc.cm.out;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Returns an unclosable view on an InputStream.
 *
 * @author Florian Kelbert
 *
 */
public class UnclosableInputStream extends FilterInputStream {

	public UnclosableInputStream(InputStream in) {
		super(in);
	}
	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException();
	}
}
