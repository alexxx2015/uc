package de.tum.in.i22.pep2pdp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.EventBasic;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.Event;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status;
import de.tum.in.i22.pdp.gpb.PdpProtos.Status.EStatus;

public class Pep2PdpFastImp implements IPep2PdpFast {
	
	private Logger _logger = Logger.getRootLogger();
	
	private String _address;
	private int _port;
	private Socket _clientSocket;
	private OutputStream _output;
 	private InputStream _input;

 	public Pep2PdpFastImp(String address, int port) {
		_address = address;
		_port = port;
	}
 	
	public EStatus notifyEvent(IEvent event) {
		_logger.debug("Creating Google Protocol Buffer");
		Event e = EventBasic.createGpbEvent(event);
		
		try {
			e.writeDelimitedTo(_output);
			_output.flush();
			
			_logger.debug("Event written to OutputStream.");
			
			Status status = Status.parseDelimitedFrom(_input);
			return status.getValue();
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
		}
		
		return null;
	}

	public void connect() throws Exception {
		_logger.debug("Establishing connection to " + _address + ":" + _port + " .>");
		_clientSocket = new Socket(_address, _port);
		
		try {
			_logger.debug("Getting i/o streams .>");
			_output = _clientSocket.getOutputStream();
			_input = _clientSocket.getInputStream();
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	public void disconnect() {
		_logger.info("Tearing down the connection .>s");
		if (_clientSocket != null) {
			try {
				_input.close();
				_output.close();
				_clientSocket.close();
				_clientSocket = null;
				_logger.info("Connection closed!");
			} catch (IOException e) {
				_logger.error("Error occurred when closing the connection.", e);
			}
		}
	}
}
