//package de.tum.in.i22.uc.cm.in.thrift;
//
//import java.io.EOFException;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.thrift.TException;
//
//import de.tum.i22.in.uc.cm.thrift.Event;
//import de.tum.i22.in.uc.cm.thrift.Pxp;
//import de.tum.i22.in.uc.cm.thrift.Response;
//import de.tum.i22.in.uc.cm.thrift.StatusType;
//import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
//import de.tum.in.i22.uc.cm.basic.EventBasic;
//import de.tum.in.i22.uc.cm.datatypes.IEvent;
//import de.tum.in.i22.uc.cm.datatypes.IResponse;
//import de.tum.in.i22.uc.cm.in.ClientConnectionHandler;
//import de.tum.in.i22.uc.cm.in.IForwarder;
//import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
//import de.tum.in.i22.uc.cm.in.RequestHandler;
//import de.tum.in.i22.uc.cm.requests.PdpRequest;
//import de.tum.in.i22.uc.cm.requests.PdpRequest.EPdpRequestType;
//
//public class CopyOfTAny2PdpHandler extends ClientConnectionHandler implements TAny2Pdp.Iface, IForwarder {
//
//	private final int _port;
//
//	public CopyOfTAny2PdpHandler(int pepPort) {
//		super(null, null);
//		_port = pepPort;
//	}
//
//	@Override
//	public String toString() {
//		return this.getClass().getSimpleName() + " listening on port " + _port;
//	}
//
//	private IResponse processEvent(IEvent ev) {
//		RequestHandler.getInstance().addRequest(new PdpRequest(EPdpRequestType.NOTIFY_EVENT, ev), this);
//
//		Object responseObj;
//		try {
//			responseObj = waitForResponse();
//			_logger.error("Communication error. Returning null");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		if (responseObj instanceof IResponse) {
//			return (IResponse) responseObj;
//		} else {
//			throw new RuntimeException("IResponse type expected for " + responseObj);
//		}
//	}
//
//	/****
//	 * Async events are not blocking on the PEP side, therefore they can only be actual events.
//	 *
//	 */
//	public void processEventAsync(Event e) throws TException {
//		if (e == null) {
//			_logger.error("received null event, replying with null response");
//			return;
//		}
//
//		IEvent ev = new EventBasic(e.name, e.parameters, true);
//		_logger.trace("PDP received asynchronous thrift event " + e.name);
//		processEvent(ev);
//		return;
//	}
//
//	/****
//	 * Async events are not blocking on the PEP side, therefore they can only be actual events.
//	 * As of now, we assume the sync (=blocking) events are ONLY DESIRED events, but for synchronization issues it may be the case that we need to make also the actual events synchronous.
//	 * Note that, however, the events are processed in the same order in which they are received by the queue.
//	 * This means that even if the actual event is not blocking, all the actual events will be processed in the same order they have been received and in case of a desired event, th event won't be allowed to be exectued until all the async events before have been processed
//	 */
//	public Response processEventSync(Event e) throws TException {
//		if (e == null) {
//			_logger.error("received null event, replying with null response");
//			return null;
//		}
//
//		IEvent ev = new EventBasic(e.name, e.parameters, false);
//		_logger.trace("PDP received synchronous thrift event " + e.name);
//
//		IResponse response = processEvent(ev);
//
//		_logger.trace("Response to return: " + response);
//
//		Response finalThriftResponse;
//
//		switch (response.getAuthorizationAction().getEStatus()) {
//		case INHIBIT:
//			finalThriftResponse = new Response(StatusType.INHIBIT);
//			break;
//		case ALLOW:
//			finalThriftResponse = new Response(StatusType.ALLOW);
//
//			// TODO: This needs to be changed. The correct behavior in theory is
//			// that the PEP re-send another actual event when things actually
//			// happen. For the time being, we pretend everything goes fine when
//			// we allow and thus we notify the actual event to the pip.
//			_logger.trace("Event " + e.name+" is allowed. Let's notify the PIP about it.");
//			throwAwayResponse();
//			processEvent(new EventBasic(e.name, e.parameters, true));
//			break;
//		case MODIFY:
//			// TODO: Add modification action cause it is not supported on
//			// the PEP side yet
//			finalThriftResponse = new Response(StatusType.MODIFY);
//			break;
//		default:
//			finalThriftResponse = new Response(StatusType.ERROR);
//			finalThriftResponse.setComment("Error. Answer is " + response);
//		}
//		throwAwayResponse();
//		return finalThriftResponse;
//	}
//
//	@Override
//	protected void doProcessing() throws IOException, EOFException,
//			InterruptedException, MessageTooLargeException {
//		_logger.debug("Thrift doProcessing invoked");
//
//		// TODO Auto-generated method stub
//
//	}
//
//
//	@Override
//	protected void disconnect() {
//	}
//
//
//	@Override
//	public Response notifyEvent(Event e) throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean registerPxp(Pxp pxp) throws TException {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public StatusType deployMechanism(String mechanism) throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public StatusType revokeMechanism1(String policyName) throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public StatusType revokeMechanism2(String policyName, String mechName)
//			throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public StatusType deployPolicy(String policyFilePath) throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Map<String, List<String>> listMechanisms() throws TException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//}
