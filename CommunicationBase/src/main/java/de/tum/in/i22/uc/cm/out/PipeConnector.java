package de.tum.in.i22.uc.cm.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
	public void connect() throws IOException {
		_logger.debug("Establish connection to pipes " + _inPipe + " and " + _outPipe);

		try {
			_logger.debug("Get i/o streams.");
			_outputStream = new BufferedOutputStream(new FileOutputStream(_outPipe));
			_inputStream = new BufferedInputStream(new FileInputStream(_inPipe));
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	@Override
	public void disconnect() {
		_logger.info("Tear down the connection");
		try {
			_inputStream.close();
			_outputStream.close();
			_logger.info("Connection closed!");
		} catch (IOException e) {
			_logger.error("Error occurred when closing the connection.", e);
		}
	}
}
