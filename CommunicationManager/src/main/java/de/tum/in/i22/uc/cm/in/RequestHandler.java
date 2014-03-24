package de.tum.in.i22.uc.cm.in;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.in.i22.uc.cm.in.thrift.GenericThriftServer;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PdpHandler;
import de.tum.in.i22.uc.cm.requests.PdpRequest;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.cm.requests.PmpRequest;
import de.tum.in.i22.uc.cm.requests.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.core.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class RequestHandler implements Runnable {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	public final static RequestHandler INSTANCE = new RequestHandler();

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper<? extends Request>> _requestQueue = new LinkedBlockingQueue<>();

	public static final PdpHandler PDP = PdpHandler.getInstance();
	public static final PipHandler PIP = PipHandler.getInstance();
	public static final PmpHandler PMP = PmpHandler.getInstance();


	private static Thread _threadThriftServer;
	private static boolean _startedThriftServer = false;



	private RequestHandler() {
		PDP.init(PIP, PMP);
		PIP.init(PDP, PMP);
		PMP.init(PIP, PDP);

		startListeners();
	}


	private void startListeners() {

		int port = Settings.getInstance().getPepThriftListenerPortNum();

		TAny2PdpHandler pdpServerHandler=new TAny2PdpHandler(port);
		TAny2Pdp.Processor<TAny2PdpHandler> pdpProcessor =	new TAny2Pdp.Processor<TAny2PdpHandler>(pdpServerHandler);
		GenericThriftServer<TAny2PdpHandler> pdpServer = new GenericThriftServer<>(port, new TAny2PdpHandler(port), pdpProcessor);
		new Thread (pdpServer).start();


	}


//	private void startPepThriftListener() {
//		if (Settings.getInstance().isPepThriftListenerEnabled()) {
//			int pepThriftListenerPort = Settings.getInstance().getPepThriftListenerPortNum();
//			_logger.info("Start GenericThriftServer on port: " + pepThriftListenerPort);
//			_threadThriftServer = new Thread (new GenericThriftServer(pepThriftListenerPort, Settings.getInstance().getPepGPBListenerPortNum()));
//			_threadThriftServer.start();
//			_startedThriftServer = true;
//		}
//	}


	public void stop(){
		// TODO These methods are deprecated for a good reason... Get rid of them.
		this._threadThriftServer.stop();
	}


	public static <T extends Request>  void addRequest(T request, IForwarder forwarder) {
		INSTANCE._requestQueue.add(new RequestWrapper<T>(request, forwarder));
	}

//	public void addUpdateIfFlowRequest(IPipDeployer pipDeployer,
//			byte[] jarBytes, EConflictResolution conflictResolutionFlag,
//			IForwarder forwarder) throws InterruptedException {
//		// add update information flow request to the tail of the queue
//		// put method blocks until the space in the queue becomes available
//		UpdateIfFlowSemanticsRequestWrapper request = new UpdateIfFlowSemanticsRequestWrapper(
//				forwarder);
//		request.setPipDeployer(pipDeployer);
//		request.setJarBytes(jarBytes);
//		request.setConflictResolution(conflictResolutionFlag);
//
//		add(request);
//	}

	@Override
	public void run() {
		_logger.debug("Request handler run method");
		while (!Thread.interrupted()) {
			RequestWrapper<? extends Request> requestWrapper = null;
			try {
				requestWrapper = _requestQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}

			Request request = requestWrapper.getRequest();
			IForwarder forwarder = requestWrapper.getForwarder();
			Object response = null;

			if (request instanceof PdpRequest) {
				response = PDP.process((PdpRequest) request);
			} else if (request instanceof PipRequest) {
				response = PIP.process((PipRequest) request);
			} else if (request instanceof PmpRequest) {
				response = PMP.process((PmpRequest) request);
			} else {
				throw new RuntimeException("Unknown queue element " + request);
			}

			_logger.trace("event " + request.toString() + " processed. forward response");
			if (forwarder != null) {
				forwarder.forwardResponse(response);
			}
			_logger.trace("response forwarded");
		}

		// the thread is interrupted, stop processing the events
	}


//	private IStatus notifyEventToPip(IEvent event) {
//		try {
//
//			return _pipHandler.notifyActualEvent(event);
//
//			// / return _mf.createStatus(EStatus.OKAY);
//		} catch (Exception e) {
//			_logger.fatal("Failed to notify actual event to PIP.", e);
//			return _mf.createStatus(EStatus.ERROR, "Error at PDP: " + e.getMessage());
//		}
//	}
//
//	private IStatus delegeteUpdateIfFlowToPip(
//			UpdateIfFlowSemanticsRequestWrapper request) {
//		_logger.debug("Delegate update if flow to PIP");
//		try {
//			_logger.debug("Establish connection to PIP");
//
//			File jarFile = new File(FileUtils.getTempDirectory(), "jarFile"
//					+ System.currentTimeMillis());
//			FileUtils.writeByteArrayToFile(jarFile, request.getJarBytes());
//			IStatus status = _pdp2PipProxy.updateInformationFlowSemantics(
//					request.getPipDeployer(), jarFile,
//					request.getConflictResolution());
//			jarFile.delete();
//
//			ConnectionManager.MAIN.release(_pdp2PipProxy);
//
//			return status;
//		} catch (Exception e) {
//			_logger.fatal("Failed to notify actual event to PIP.", e);
//			return _mf.createStatus(EStatus.ERROR, "Error at PDP: " + e.getMessage());
//		}
//	}
//
//
//
//
//	/**
//	 * Update information flow semantics request wrapper. Used for placing
//	 * requests in the queue.
//	 *
//	 * @author Stoimenov
//	 *
//	 */
//	private class UpdateIfFlowSemanticsRequestWrapper extends RequestWrapper {
//		private IPipDeployer _pipDeployer;
//		private byte[] _jarBytes;
//		private EConflictResolution _conflictResolution;
//
//		public UpdateIfFlowSemanticsRequestWrapper(IForwarder forwarder) {
//			super(forwarder);
//		}
//
//		public IPipDeployer getPipDeployer() {
//			return _pipDeployer;
//		}
//
//		public void setPipDeployer(IPipDeployer pipDeployer) {
//			_pipDeployer = pipDeployer;
//		}
//
//		public byte[] getJarBytes() {
//			return _jarBytes;
//		}
//
//		public void setJarBytes(byte[] jarBytes) {
//			_jarBytes = jarBytes;
//		}
//
//		public EConflictResolution getConflictResolution() {
//			return _conflictResolution;
//		}
//
//		public void setConflictResolution(EConflictResolution conflictResolution) {
//			_conflictResolution = conflictResolution;
//		}
//
//	}
}
