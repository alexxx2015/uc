package de.tum.in.i22.pdp.cm;

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
		try {
			_input = _socket.getInputStream();
			_output = _socket.getOutputStream();
			
			try {
				//FIXME this code works but it is not efficient (wastes CPU time)
				while (_isOpen) {
					//parse message
					Event event = Event.parseDelimitedFrom(_input);
					
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
			/* connection either terminated by the client or lost due to 
			 * network problems*/	
			catch (IOException ex) {
				_logger.error("IOException in ClientConnectionHandler.", ex);
				_isOpen = false;
			}
			
		} catch (IOException ioe) {
			_logger.error("Connection could not be established!", ioe);
		} finally {
			try {
				if (_socket != null) {
					_input.close();
					_output.close();
					_socket.close();
				}
			} catch (IOException ioe) {
				_logger.debug("Unable to tear down connection!", ioe);
			}
		}
	}
	
}
