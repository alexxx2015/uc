package de.tum.in.i22.pdp.cm.in.pep;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Template class
 * @author Stoimenov
 *
 */
public abstract class ClientConnectionHandler implements Runnable {

	protected static Logger _logger = Logger.getRootLogger();
	private Socket _socket;
	private InputStream _input;
	private ObjectInputStream _objInput;
	private OutputStream _output;
	private boolean _shouldContinue;

	protected ClientConnectionHandler(Socket socket) {
		super();
		_socket = socket;
		_shouldContinue = true;
	}

	@Override
	public void run() {
		try {
			_input = _socket.getInputStream();
			_output = _socket.getOutputStream();

			_objInput = new ObjectInputStream(_input);
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
			
		}
		catch (IOException ioe) {
			_logger.error("Connection could not be established!", ioe);
		} finally {
			try {
				if (_socket != null) {
					_input.close();
					_objInput.close();
					_output.close();
					_socket.close();
				}
			} catch (IOException ioe) {
				_logger.debug("Unable to tear down connection!", ioe);
			}
		}
	}
	
	protected OutputStream getOutputStream() {
		return _output;
	}
	
	protected ObjectInputStream getObjectInputStream() {
		return _objInput;
	}
	
	protected abstract void doProcessing() throws IOException, EOFException, InterruptedException;
	
	@Override
	public String toString() {
		String string = _socket.getInetAddress().getHostName() + " on port "
				+ _socket.getPort();
		return string;
	}

}