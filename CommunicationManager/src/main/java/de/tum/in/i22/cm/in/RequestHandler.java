package de.tum.in.i22.cm.in;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import de.tum.in.i22.cm.in.pdp.PdpHandler;
import de.tum.in.i22.cm.in.pep.thrift.ThriftServer;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PdpRequest;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.cm.requests.PmpRequest;
import de.tum.in.i22.uc.cm.requests.Request;
import de.tum.in.i22.uc.cm.settings.Settings;

public class RequestHandler implements Runnable {

	private static Logger _logger = Logger.getRootLogger();

	public final static RequestHandler INSTANCE = new RequestHandler();

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper<? extends Request>> _requestQueue = new LinkedBlockingQueue<>();

	private final IAny2Pdp _pdpHandler;
	private final IAny2Pip _pipHandler;
	private final IAny2Pmp _pmpHandler;


	private static Thread _threadThriftServer;
	private static boolean _startedThriftServer = false;



	private RequestHandler() {
		_pdpHandler = PdpHandler.getInstance();
		_pipHandler = null;
		_pmpHandler = null;

		_pdpHandler.init(_pipHandler, _pmpHandler);
		_pipHandler.init(_pdpHandler, _pmpHandler);
		_pmpHandler.init(_pipHandler, _pdpHandler);

		startListeners();
	}


	private void startListeners() {
		// TODO start thrift server listeners here.

	}


//	private void startPepThriftListener() {
//		if (Settings.getInstance().isPepThriftListenerEnabled()) {
//			int pepThriftListenerPort = Settings.getInstance().getPepThriftListenerPortNum();
//			_logger.info("Start ThriftServer on port: " + pepThriftListenerPort);
//			_threadThriftServer = new Thread (new ThriftServer(pepThriftListenerPort, Settings.getInstance().getPepGPBListenerPortNum()));
//			_threadThriftServer.start();
//			_startedThriftServer = true;
//		}
//	}


	public void stop(){
		// TODO These methods are deprecated for a good reason... Get rid of them.
		this._threadThriftServer.stop();
	}

	public <T extends Request> void addRequest(T request, IForwarder forwarder) {
		_requestQueue.add(new RequestWrapper<T>(request, forwarder));
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
				response = _pdpHandler.process((PdpRequest) request);
			} else if (request instanceof PipRequest) {
				response = _pipHandler.process((PipRequest) request);
			} else if (request instanceof PmpRequest) {
				response = _pmpHandler.process((PmpRequest) request);
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
