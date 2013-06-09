package de.tum.in.i22.pmp2pdp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class Pmp2PdpFastImp implements IPmp2PdpFast {
	private Logger _logger = Logger.getRootLogger();
	
	private String _address;
	private int _port;
	private Socket _clientSocket;
	private OutputStream _output;
	private ObjectOutputStream _objOutput;
 	private InputStream _input;

 	public Pmp2PdpFastImp(String address, int port) {
		_address = address;
		_port = port;
	}


	public void connect() throws Exception {
		_logger.debug("Establish connection to " + _address + ":" + _port);
		_clientSocket = new Socket(_address, _port);
		
		try {
			_logger.debug("Get i/o streams.");
			_output = _clientSocket.getOutputStream();
			_objOutput = new ObjectOutputStream(_output);
			_input = _clientSocket.getInputStream();
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	public void disconnect() {
		_logger.info("Tear down the connection");
		if (_clientSocket != null) {
			try {
				_input.close();
				_output.close();
				_objOutput.close();
				_clientSocket.close();
				_clientSocket = null;
				_logger.info("Connection closed!");
			} catch (IOException e) {
				_logger.error("Error occurred when closing the connection.", e);
			}
		}
	}

	public EStatus deployMechanism(IMechanism mechanism) {
		_logger.debug("Create Google Protocol Buffer Mechanism instance");
		GpMechanism gpMechanism = MechanismBasic.createGpbMechanism(mechanism);
		
		try {
			int messageSize = gpMechanism.getSerializedSize();
			_logger.debug("GPB message size: " + messageSize);
//			if (messageSize > 1024) {
//				throw new RuntimeException("Message too big! Message size: " + messageSize);
//			}
			_objOutput.writeInt(messageSize);
			_objOutput.write(gpMechanism.toByteArray());
			_objOutput.flush();
			
			_logger.debug("Event written to OutputStream.");
			
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(_input);
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
		}
		
		return null;
	}

	public EStatus exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	public EStatus revokeMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}
}
