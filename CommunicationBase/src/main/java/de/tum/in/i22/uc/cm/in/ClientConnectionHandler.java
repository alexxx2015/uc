package de.tum.in.i22.uc.cm.in;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class ClientConnectionHandler implements IForwarder, Runnable {

	protected static Logger _logger = Logger.getLogger(ClientConnectionHandler.class);

	private final DataInputStream _inputStream;
	private final OutputStream _outputStream;

	protected boolean _shouldContinue = true;

	protected Object _response = null;

	public ClientConnectionHandler(DataInputStream inputStream, OutputStream outputStream) {
		_inputStream = inputStream;
		_outputStream = outputStream;
	}


	@Override
	public void run() {
		try {
			try {
				while (_shouldContinue) {
					doProcessing();
				}
			}
			catch (InterruptedException e) {
				_logger.error("Thread interrupted. Cannot server the client request.", e);
				_shouldContinue = false;
			}
			catch (EOFException eof) {
				_logger.debug("End of stream reached.");
				_shouldContinue = false;
			}
			/* connection either terminated by the client or lost due to
			 * network problems*/
			catch (IOException ex) {
				_logger.error("Connection lost.", ex);
				_shouldContinue = false;
			}
			catch (MessageTooLargeException etl) {
				_logger.error("Incoming message to large.", etl);
				_logger.info("The connection to the client will be interrupted.");
				_shouldContinue = false;
			}

		} finally {
			try {
				disconnect();
				if (_inputStream != null) {
					_inputStream.close();
				}
				if (_outputStream != null) {
					_outputStream.close();
				}
			} catch (IOException ioe) {
				_logger.debug("Unable to close connection!", ioe);
			}
		}
	}

	protected Object waitForResponse() throws InterruptedException {
		synchronized (this) {
			while (_response == null) {
				_logger.debug("Wait for the event to be processed.");
				wait();
			}
			return _response;
		}
	}

	@Override
	public void forwardResponse(Object response) {
		_logger.debug("Response to forward: " + response + ". Wake up the thread.");
		synchronized (this) {
			_response = response;
			notifyAll();
		}
	}

	protected Object getResponse() {
		return _response;
	}

	/**
	 * Sets response to null
	 */
	protected void throwAwayResponse() {
		_response = null;
	}

	protected OutputStream getOutputStream() {
		return _outputStream;
	}

	public DataInputStream getDataInputStream() {
		return _inputStream;
	}

	protected abstract void doProcessing() throws IOException,
		EOFException, InterruptedException, MessageTooLargeException;

	protected abstract void disconnect();
}
