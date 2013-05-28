package de.tum.in.i22.pdp.cm;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.gpb.PdpProtos.Event;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;

public class ClientConnectionHandler {
	private static Logger _logger = Logger.getRootLogger();
	private Socket _socket;
	private InputStream _input;
	private OutputStream _output;
	
	private boolean _isOpen;
	
	public ClientConnectionHandler(Socket socket) {
		super();
		_socket = socket;
		_isOpen = true;
	}
	
	public void start() {
		ObjectInputStream _objInp = null;
		try {
			_input = _socket.getInputStream();
			_output = _socket.getOutputStream();
			
			_objInp = new ObjectInputStream(_input);
			try {
				while (_isOpen) {
					int messageSize = _objInp.readInt();
					if (messageSize > 1024) {
						_logger.debug("Message size to big: " + messageSize);
						throw new RuntimeException("Message too big! Message size: " + messageSize);
					}
					
					byte[] bytes = new byte[messageSize];
					_objInp.readFully(bytes);
					//parse message
					Event event = Event.parseFrom(bytes);					
					if (event != null) {
						//TODO process event
						_logger.debug("Received event: " + event);
						
						Status.Builder status = Status.newBuilder();
						//TODO return status
						status.setValue(EStatus.ALLOW);
						status.build().writeDelimitedTo(_output);
						_output.flush();
					} else {
						_logger.debug("Received event is null.");
					}
				}
			}
			
			catch (EOFException eof) {
				_logger.error("End of stream reached.");
				_isOpen = false;
			}
			/* connection either terminated by the client or lost due to 
			 * network problems*/	
			catch (IOException ex) {
				_logger.error("Connectio lost.");
				_isOpen = false;
			}
			
		} catch (IOException ioe) {
			_logger.error("Connection could not be established!", ioe);
		} finally {
			try {
				if (_socket != null) {
					_input.close();
					_objInp.close();
					_output.close();
					_socket.close();
				}
			} catch (IOException ioe) {
				_logger.debug("Unable to tear down connection!", ioe);
			}
		}
	}
	
}
