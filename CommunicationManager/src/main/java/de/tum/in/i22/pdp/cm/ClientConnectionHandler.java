package de.tum.in.i22.pdp.cm;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.EventHandler;
import de.tum.in.i22.pdp.IResponder;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class ClientConnectionHandler implements Runnable, IResponder {
	private static Logger _logger = Logger.getRootLogger();
	private Socket _socket;
	private InputStream _input;
	private OutputStream _output;
	
	private boolean _isOpen;
	
	private EStatus _status = null;
	
	public ClientConnectionHandler(Socket socket) {
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
					GpEvent gpEvent = GpEvent.parseFrom(bytes);					
					if (gpEvent != null) {
						//TODO process event
						_logger.trace("Received event: " + gpEvent);
						
						EventHandler eventHandler = EventHandler.getInstance();
						
						IMessageFactory mf = MessageFactory.getInstance();
						IEvent event = mf.createEvent(gpEvent, System.currentTimeMillis());
						eventHandler.addEvent(event, this);
						
						synchronized(this) {
							while (_status == null) {
								_logger.debug("Wait for the event to be processed.");
								wait();
							}
						}
						
						_logger.debug("Status to return: " + _status);
						
						GpStatus.Builder status = GpStatus.newBuilder();
						status.setValue(_status);
						_status = null;
						status.build().writeDelimitedTo(_output);
						_output.flush();
					} else {
						_logger.debug("Received event is null.");
					}
				}
			}
			catch (InterruptedException e) {
				_logger.error("Thread interrupted. Cannot server the client request.", e);
				return;
			}
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

	public void forwardResponse(EStatus status) {
		_logger.debug("Wake up the thread.");
		synchronized(this) {
			_status = status;
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
