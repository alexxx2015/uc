package de.tum.in.i22.pdp.cm.in.pep;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Florian Kelbert
 *
 */
public class PepClientPipeConnectionHandler extends PepClientConnectionHandler {

	private final File _inPipe;
	private final File _outPipe;

	// we do not make use of the superclasses' streams on purpose!
	private DataInputStream _inputStream;
	private OutputStream _outputStream;

	// We invoke the superconstructor with (null, null).
	// Reason: The invocations to FileInputStream/FileOutputStream (cf. run())
	// block until connections to the pipes are made.
	// Therefore it is useful to start the thread right away and not block the constructor.
	// Streams are then instantiated within the run() method.
	// -FK-
	public PepClientPipeConnectionHandler(File inPipe, File outPipe) throws FileNotFoundException {
		super(null, null);
		_inPipe = inPipe;
		_outPipe = outPipe;
	}

	@Override
	public void run() {
		try {
			_inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(_inPipe)));
			_outputStream = new BufferedOutputStream(new FileOutputStream(_outPipe));
		} catch (FileNotFoundException e) {
			_logger.warn("Pipe does not exist.", e);
			return;
		}
		super.run();
	}

	@Override
	public DataInputStream getDataInputStream() {
		return _inputStream;
	}

	@Override
	protected OutputStream getOutputStream() {
		return _outputStream;
	}

	@Override
	protected void disconnect() {
		try {
			if (_inputStream != null) {
				_inputStream.close();
			}
			if (_outputStream != null) {
				_outputStream.close();
			}
		} catch (IOException e) {
			_logger.warn("Unablet to close stream", e);
		}
	}
}
