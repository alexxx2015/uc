package de.tum.in.i22.uc.cm.in;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import com.google.common.base.Objects;


/**
 * Template class
 * @author Stoimenov / Florian Kelbert
 *
 */
public abstract class ClientTcpConnectionHandler extends ClientConnectionHandler {

	private final Socket _socket;

	public ClientTcpConnectionHandler(Socket socket) throws IOException {
		super(new DataInputStream(new BufferedInputStream(socket.getInputStream())),
				new BufferedOutputStream(socket.getOutputStream()));
		_socket = socket;
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

		}
	}

	@Override
	protected void disconnect() {
		try {
			if (_socket != null) {
				_socket.close();
			}
		} catch (IOException ioe) {
			_logger.debug("Unable to tear down connection!", ioe);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("_socket", _socket)
				.add("_shouldContinue", _shouldContinue)
				.add("_response", _response)
				.toString();
	}

}