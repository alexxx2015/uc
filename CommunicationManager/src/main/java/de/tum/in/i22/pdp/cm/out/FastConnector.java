package de.tum.in.i22.pdp.cm.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.google.protobuf.MessageLite;

public abstract class FastConnector implements IFastConnector {
	
	protected static Logger _logger = Logger.getLogger(FastConnector.class);
	
	private String _address;
	private int _port;
	private Socket _clientSocket;
	private OutputStream _outputStream;
 	private InputStream _inputStream;

 	public FastConnector(String address, int port) {
		_address = address;
		_port = port;
	}

	@Override
	public void connect() throws Exception {
		_logger.debug("Establish connection to " + _address + ":" + _port);
		_clientSocket = new Socket(_address, _port);
		
		try {
			_logger.debug("Get i/o streams.");
			_outputStream = _clientSocket.getOutputStream();
			_inputStream = _clientSocket.getInputStream();
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	@Override
	public void disconnect() {
		_logger.info("Tear down the connection");
		if (_clientSocket != null) {
			try {
				_inputStream.close();
				_outputStream.close();
				_clientSocket.close();
				_clientSocket = null;
				_logger.info("Connection closed!");
			} catch (IOException e) {
				_logger.error("Error occurred when closing the connection.", e);
			}
		}
	}
	
	/**
	 * This method is currently not used. The idea was to use it instead of
	 *  Google Protocol Buffer method writeDelimitedTo().
	 * It first writes the operation type (one byte), then the size of the message as 32 bit int
	 *  and then it writes the message bytes. The message size always takes 32 bits. WriteDelimitedTo()
	 *  method uses compact representation of int.
	 * @param operationType
	 * @param messages
	 * @throws IOException
	 */
	protected void sendData(byte operationType, MessageLite... messages) 
			throws IOException {
		_logger.trace("Write operation type. Byte representation: " + operationType);
		getOutputStream().write(operationType);
		sendData(messages);
		getOutputStream().flush();
	}
	
	private void sendData(MessageLite... messages) throws IOException {
		_logger.trace("Send GPB message/s");
		if (messages != null && messages.length > 0) {
			_logger.trace("Num of messages" + messages.length);
			for (int i = 0; i < messages.length; i++) {
				int messageSize = messages[i].getSerializedSize();
				_logger.trace("Message size to send: " + messageSize);
				OutputStream out = getOutputStream();
				writeInt(out, messageSize);
				out.write(messages[i].toByteArray());
			}
		}
	}
	
	/**
	 * Currently not used.
	 * Writes 4 bytes (int as 4 bytes, Big Endian format, most significant byte first)
	 * @param out OutputStream where the data will be written
	 * @param value int value
	 * @throws IOException
	 */
	private void writeInt(OutputStream out, int value) 
			throws IOException {
		
		_logger.trace("Writing int value (" + value + ") as 4 bytes in Big Endian format");
		
		int ibyte;
        ibyte = ((value >>> 24) & 0xff); out.write(ibyte);
        ibyte = ((value >>> 16) & 0xff); out.write(ibyte);
        ibyte = ((value >>> 8) & 0xff); out.write(ibyte);
        ibyte = (value & 0xff); out.write(ibyte);
	}
	
	protected OutputStream getOutputStream() {
		return _outputStream;
	}
	
	protected InputStream getInputStream() {
		return _inputStream;
	}
}
