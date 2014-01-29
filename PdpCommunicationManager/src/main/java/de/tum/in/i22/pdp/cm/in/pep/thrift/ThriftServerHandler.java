package de.tum.in.i22.pdp.cm.in.pep.thrift;

import java.io.EOFException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;

public class ThriftServerHandler extends ClientConnectionHandler implements
		PDPThriftConnector.Iface {

	private static RequestHandler requestHandler = RequestHandler.getInstance();
	private static final String IP = "localhost";
	private static int PORT=8090;

	public ThriftServerHandler(int pepPort) {
		// TODO Auto-generated constructor stub
		// we should start it on this port
		super(null);
		PORT = pepPort;
		requestHandler = RequestHandler.getInstance();
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ThriftServer: " + IP + ":" + PORT;
	}

	private static final Logger _logger = Logger
			.getLogger(ThriftServerHandler.class);

	@Override
	public void processEventAsync(Event e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("processEventAsync invoked, but not supported");
		// Not needed
	}

	@Override
	public Response processEventSync(Event e) throws TException {
		if (e == null) {
			_logger.error("received null event, replying with null response");
			return null;
		}

		IEvent ev = new EventBasic(e.name, e.parameters, false);
		_logger.trace("PDP received thrift event " + e.name);
		
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();

		Object responseObj;
		try {
			requestHandler.addEvent(ev, this);
			responseObj = waitForResponse();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			_logger.error("Communication error. Returning null");
			e1.printStackTrace();
			return null;
		}

		if (responseObj instanceof IResponse) {
			IResponse response = (IResponse) responseObj;
			_logger.trace("Response to return: " + response);

			Response finalThriftResponse;

			switch (response.getAuthorizationAction().getEStatus()) {
			case INHIBIT:
				finalThriftResponse = new Response(StatusType.INHIBIT);

			case ALLOW:
				finalThriftResponse = new Response(StatusType.ALLOW);

			case MODIFY:
				// TODO: Add modification action cause it is not supported on
				// the PEP side yet
				finalThriftResponse = new Response(StatusType.MODIFY);
			default:
				finalThriftResponse = new Response(StatusType.ERROR);
				finalThriftResponse.setComment("Error. Answer is " + response);
			}
			throwAwayResponse();
			return finalThriftResponse;

		} else {
			throw new RuntimeException("IResponse type expected for "
					+ responseObj);
		}
	}

	@Override
	public void forwardResponse(Object response) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doProcessing() throws IOException, EOFException,
			InterruptedException, MessageTooLargeException {
		// TODO Auto-generated method stub

	}
}
