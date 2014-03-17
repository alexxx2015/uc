package de.tum.in.i22.uc.cm.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Florian Kelbert
 *
 */
public class PipeConnector extends Connector {

	private final File _inPipe;
	private final File _outPipe;

 	public PipeConnector(File inPipe, File outPipe) {
		_inPipe = inPipe;
		_outPipe = outPipe;
	}

	@Override
	void connect() throws IOException {
		_logger.debug("Establish connection to pipes " + _inPipe + " and " + _outPipe);

		try {
			_logger.debug("Get i/o streams.");
			setOutputStream(new FileOutputStream(_outPipe));
			setInputStream(new FileInputStream(_inPipe));
			_logger.debug("Connection established.");
		} catch(FileNotFoundException e) {
			_logger.debug("File not found.", e);
			throw e;
		}
	}

	@Override
	void disconnect() {
		_logger.info("Tear down the pipe");
		try {
			close();
			_logger.info("Pipe closed!");
		} catch (IOException e) {
			_logger.error("Error occurred when closing the pipe.", e);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(_inPipe, _outPipe);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PipeConnector) {
			PipeConnector o = (PipeConnector) obj;
			return Objects.equals(_inPipe, o._inPipe)
					&& Objects.equals(_outPipe, o._outPipe);
		}
		return false;
	}
}
