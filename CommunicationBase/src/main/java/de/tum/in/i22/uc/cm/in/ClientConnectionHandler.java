package de.tum.in.i22.uc.cm.in;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;


/**
 * Template class
 * @author Stoimenov
 *
 */
public abstract class ClientConnectionHandler implements Runnable, IForwarder {

	protected static Logger _logger = Logger.getLogger(ClientConnectionHandler.class);
	private Socket _socket;
	private InputStream _inputStream;
	private OutputStream _outputStream;
	private boolean _shouldContinue;
	
	private Object _response = null;

	protected ClientConnectionHandler(Socket socket) {
		super();
		_socket = socket;
		_shouldContinue = true;
	}

	@Override
	public void run() {
		try {
			_inputStream = _socket.getInputStream();
			_outputStream = _socket.getOutputStream();
			
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
				_logger.warn("End of stream reached.");
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
			
		}
		catch (IOException ioe) {
			_logger.error("Connection could not be established!", ioe);
		} finally {
			try {
				if (_socket != null) {
					_inputStream.close();
					_outputStream.close();
					_socket.close();
				}
			} catch (IOException ioe) {
				_logger.debug("Unable to tear down connection!", ioe);
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
		return new DataInputStream(_inputStream);
	}
	
	protected abstract void doProcessing() throws IOException,
		EOFException, InterruptedException, MessageTooLargeException;
	
	@Override
	public String toString() {
		String string = _socket.getInetAddress().getHostName() + " on port "
				+ _socket.getPort();
		return string;
	}

}