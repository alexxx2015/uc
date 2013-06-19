package de.tum.in.i22.pdp.cm.in.pmp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.IForwarder;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpMechanism;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class PmpClientConnectionHandler 
	implements Runnable, IForwarder{

	private static Logger _logger = Logger.getRootLogger();
	private Socket _socket;
	private InputStream _input;
	private OutputStream _output;
	
	private boolean _isOpen;
	
//	private IResponse _response = null;
	
	public PmpClientConnectionHandler(Socket socket) {
		super();
		_socket = socket;
		_isOpen = true;
	}
	
	public void run() {
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
					GpMechanism gpMechanism = GpMechanism.parseFrom(bytes);					
					if (gpMechanism != null) {
						_logger.trace("Received mechanism: " + gpMechanism);
						
//						EventHandler eventHandler = EventHandler.getInstance();
//						
//						IMessageFactory mf = MessageFactory.getInstance();
//						IEvent event = mf.createEvent(gpEvent, System.currentTimeMillis());
//						eventHandler.addEvent(event, this);
						
//						synchronized(this) {
//							while (_response == null) {
//								_logger.debug("Wait for the event to be processed.");
//								wait();
//							}
//						}
						
//						_logger.trace("Response to return: " + _response);
//						
//						GpResponse gpResponse = ResponseBasic.createGpbResponse(_response);
//						_response = null;
//						gpResponse.writeDelimitedTo(_output);
//						_output.flush();
						
						GpStatus.Builder status = GpStatus.newBuilder();
						status.setValue(EStatus.OKAY);
						status.build().writeDelimitedTo(_output);
						_output.flush();
					} else {
						_logger.debug("Received event is null.");
					}
				}
			}
//			catch (InterruptedException e) {
//				_logger.error("Thread interrupted. Cannot server the client request.", e);
//				return;
//			}
			catch (EOFException eof) {
				_logger.warn("End of stream reached");
				_isOpen = false;
			}	
			/* connection either terminated by the client or lost due to 
			 * network problems*/
			catch (IOException ex) {
				_logger.error("Connectio lost.");
				_isOpen = false;
			}
			
		}
		catch (IOException ioe) {
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

	@Override
	public void forwardResponse(IResponse response) {
		_logger.debug("Wake up the thread.");
		synchronized(this) {
//			_response = response;
			notifyAll();
		}
	}
	
	@Override
	public String toString() {
		String string = _socket.getInetAddress().getHostName()
				+ " on port " + _socket.getPort();
		return string;
	}
}
