package de.tum.in.i22.pmp2pdp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.EPmp2PdpMethod;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;

/**
 * Each method writes a message to the output stream. 
 * First an {@link EPmp2PdpMethod} is written that identifies the method.
 * Then the message size is written as int.
 * Finally, the message is written.
 * @author Stoimenov
 *
 */
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
		_logger.debug("Deploy mechanism");
		_logger.trace("Create Google Protocol Buffer Mechanism instance");
		GpMechanism gpMechanism = MechanismBasic.createGpbMechanism(mechanism);
		
		try {
			int messageSize = gpMechanism.getSerializedSize();
			_logger.trace("GPB message size: " + messageSize);
//			if (messageSize > 1024) {
//				throw new RuntimeException("Message too big! Message size: " + messageSize);
//			}
			_objOutput.writeByte(EPmp2PdpMethod.DEPLOY_MECHANISM.getValue());
			_objOutput.writeInt(messageSize);
			_objOutput.write(gpMechanism.toByteArray());
			_objOutput.flush();
			_logger.trace("GpMechanism written to OutputStream");
			
			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(_input);
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Deploy mechanism failed.", ex);
			return null;
		}
	}

	public IMechanism exportMechanism(String par) {
		_logger.debug("Export mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = createGpString(par);
		try {
			int messageSize = gpString.getSerializedSize();
			_logger.trace("GPB message size: " + messageSize);
//			if (messageSize > 1024) {
//				throw new RuntimeException("Message too big! Message size: " + messageSize);
//			}
			_objOutput.writeByte(EPmp2PdpMethod.EXPORT_MECHANISM.getValue());
			_objOutput.writeInt(messageSize);
			_objOutput.write(gpString.toByteArray());
			_objOutput.flush();
			_logger.trace("GpString written to OutputStream");
			
			_logger.trace("Wait for GpMechanism message");
			GpMechanism gpMechanism = GpMechanism.parseDelimitedFrom(_input);
			return new MechanismBasic(gpMechanism);
		} catch (IOException ex) {
			_logger.error("Export mechanism failed.", ex);
			return null;
		}
	}

	public EStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism");
		_logger.trace("Create Google Protocol Buffer GpString");
		GpString gpString = createGpString(par);
		try {
			int messageSize = gpString.getSerializedSize();
			_logger.trace("GPB message size: " + messageSize);
//			if (messageSize > 1024) {
//				throw new RuntimeException("Message too big! Message size: " + messageSize);
//			}
			_objOutput.writeByte(EPmp2PdpMethod.REVOKE_MECHANISM.getValue());
			_objOutput.writeInt(messageSize);
			_objOutput.write(gpString.toByteArray());
			_objOutput.flush();
			_logger.trace("GpString written to OutputStream");
			
			_logger.trace("Wait for GpStatus message");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(_input);
			return gpStatus.getValue();
		} catch (IOException ex) {
			_logger.error("Revoke mechanism failed.", ex);
			return null;
		}
	}
	
	private GpString createGpString(String par) {
		GpString.Builder gpStringBuilder = GpString.newBuilder();
		gpStringBuilder.setValue(par);
		return gpStringBuilder.build();
	}
}
