package de.tum.in.i22.uc.cm.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.google.protobuf.MessageLite;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class Connector {

	protected static final Logger _logger = Logger.getLogger(Connector.class);

	private OutputStream _outputStream;
 	private InputStream _inputStream;

	public UnclosableOutputStream getOutputStream() {
		return new UnclosableOutputStream(_outputStream);
	}

	public UnclosableInputStream getInputStream() {
		return new UnclosableInputStream(_inputStream);
	}

	protected void setOutputStream(OutputStream out) {
		_outputStream = new BufferedOutputStream(out);
	}

	protected void setInputStream(InputStream in) {
		_inputStream = new BufferedInputStream(in);
	}

	protected void close() throws IOException {
		_outputStream.close();
		_inputStream.close();
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


	abstract void connect() throws IOException;
	abstract void disconnect();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);
}
