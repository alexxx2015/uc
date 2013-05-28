package de.tum.in.i22.pdp.cm;

import java.io.IOException;
import java.io.InputStream;
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
	
	public ClientConnectionHandler(Socket socket) {
		super();
		_socket = socket;
	}
	
	public void start() {
		try {
			while (!_socket.isClosed()) {
				_input = _socket.getInputStream();
				//parse message
				Event event = Event.parseDelimitedFrom(_input);

				//TODO process event
				_logger.debug("Received event: " + event);
				
				Status.Builder status = Status.newBuilder();
				//TODO return status
				status.setValue(EStatus.ALLOW);
				_output = _socket.getOutputStream();
				status.build().writeDelimitedTo(_output);
				_output.flush();
			}
		} catch (IOException ex) {
			_logger.error("IOException in ClientConnectionHandler.", ex);
		} catch (Exception e) {
			_logger.error("Exception in ClientConnectionHandler.", e);
		}
	}
	
}
