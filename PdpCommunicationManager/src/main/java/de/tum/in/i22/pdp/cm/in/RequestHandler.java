package de.tum.in.i22.pdp.cm.in;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pdp.cm.in.pip.PipRequest;
import de.tum.in.i22.pdp.cm.in.pmp.PmpRequest;
import de.tum.in.i22.pdp.cm.out.pip.Pdp2PipTcpImp;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.pdp.pipcacher.PipCacherImpl;
import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.KeyBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.in.IForwarder;
import de.tum.in.i22.uc.cm.out.ConnectionManager;

public class RequestHandler implements Runnable {

	private static Logger _logger = Logger.getRootLogger();

	private final static RequestHandler _instance = new RequestHandler();

	private final IMessageFactory _mf = MessageFactoryCreator.createMessageFactory();

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper> _requestQueue = new LinkedBlockingQueue<RequestWrapper>();

	private IIncoming _pdpHandler;
	private IPdp2Pip _pdp2PipProxy;

	private final IPdpCore2PipCacher _core2pip;
	private final IPdpEngine2PipCacher _engine2pip;
	private final IPipCacher2Pip _pipHandler;


	private RequestHandler() {
		// 'misuse' the PIP's port as an ID for the PIP's database. That's fine.
		_pipHandler = new PipHandler(PdpSettings.getInstance().getPipPortNum());

		_pdp2PipProxy = new Pdp2PipTcpImp(PdpSettings.getInstance().getPipAddress(), PdpSettings.getInstance().getPipPortNum());

		// 1 implementation for 2 interfaces
		_core2pip = new PipCacherImpl(_pipHandler);
		_engine2pip = (PipCacherImpl) _core2pip;
		populate();
	}

	public IPdpCore2PipCacher getCore2Pip(){
		return _core2pip ;
	}
	public IPipCacher2Pip getPipHandler(){
		return _pipHandler;
	}

	/***
	 * Populate the pip for testing purposes only (I know, this is not the
	 * correct place, but it's well factored out from the rest) -E-
	 */
	private void populate() {

		IKey _test_predicate_key = KeyBasic.createNewKey();
		String _test_predicate = "isNotIn|TEST_D|TEST_C|0";
		IMessageFactory _messageFactory = MessageFactoryCreator
				.createMessageFactory();

		Map<String, IKey> predicates = new HashMap<String, IKey>();
		predicates.put(_test_predicate, _test_predicate_key);
		_logger.debug("TEST: adding predicate via _core2pip");
		_core2pip.addPredicates(predicates);

		// Initialize if model with TEST_C --> TEST_D
		_logger.debug("TEST: Initialize if model with TEST_C-->TEST_D");
		IEvent initEvent = _messageFactory.createActualEvent(
				"SchemaInitializer", new HashMap<String, String>());
		_pipHandler.notifyActualEvent(initEvent);
	}

	public static RequestHandler getInstance() {
		return _instance;
	}

	public void setPdpHandler(IIncoming pdpHandler) {
		_pdpHandler = pdpHandler;
		_pdpHandler.setPdpCore2PipCacher(_core2pip);
		_pdpHandler.setPdpEngine2PipCacher(_engine2pip);
	}

	/**
	 * Helper method to synchronize access to the queue.
	 * @param obj the object to add to the queue.
	 */
	private void add(RequestWrapper obj) {
		// put method blocks until the space in the queue becomes available
		_logger.debug("Add " + obj + " to the queue.");
		_requestQueue.add(obj);
	}

	public void addEvent(IEvent event, IForwarder forwarder) throws InterruptedException {
		add(new PepNotifyEventRequestWrapper(forwarder, event));
	}

	public void addPipRequest(PipRequest request, IForwarder forwarder) throws InterruptedException {
		add(new PipRequestWrapper(forwarder, request));
	}

	public void addPmpRequest(PmpRequest request, IForwarder forwarder) throws InterruptedException {
		add(new PmpRequestWrapper(forwarder, request));
	}

	public void addUpdateIfFlowRequest(IPipDeployer pipDeployer,
			byte[] jarBytes, EConflictResolution conflictResolutionFlag,
			IForwarder forwarder) throws InterruptedException {
		// add update information flow request to the tail of the queue
		// put method blocks until the space in the queue becomes available
		UpdateIfFlowSemanticsRequestWrapper request = new UpdateIfFlowSemanticsRequestWrapper(
				forwarder);
		request.setPipDeployer(pipDeployer);
		request.setJarBytes(jarBytes);
		request.setConflictResolution(conflictResolutionFlag);

		add(request);
	}

	@Override
	public void run() {
		_logger.debug("Request handler run method");
		while (!Thread.interrupted()) {
			RequestWrapper request = null;
			try {
				request = _requestQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}

			Object response = null;
			if (request instanceof PepNotifyEventRequestWrapper) {
				/***
				 *
				 * This code has been removed because redundant with the
				 * CACHER2PIP communication
				 *
				 * if (event.isActual()) {
				 *
				 * // event is actual, send it to PIP notifyEventToPip(event); }
				 *
				 */

				response = _pdpHandler.notifyEvent(((PepNotifyEventRequestWrapper) request).getEvent());
			} else if (request instanceof PipRequestWrapper) {
				response = processPipRequest(((PipRequestWrapper) request).getPipRequest());
			} else if (request instanceof PmpRequestWrapper) {
				response = processPmpRequest(((PmpRequestWrapper) request).getPmpRequest());
			} else if (request instanceof UpdateIfFlowSemanticsRequestWrapper) {
				response = delegeteUpdateIfFlowToPip((UpdateIfFlowSemanticsRequestWrapper) request);
			} else {
				throw new RuntimeException("Unknown queue element " + request);
			}

			_logger.trace("event " + request.toString() + " processed. forward response");
			IForwarder forwarder = request.getForwarder();
			if (forwarder != null) {
				forwarder.forwardResponse(response);
			}
			_logger.trace("response forwarded");
		}

		// the thread is interrupted, stop processing the events
	}

	private Object processPmpRequest(PmpRequest pmpRequest) {
		_logger.debug("Process event " + pmpRequest);
		Object result = null;
		switch (pmpRequest.getMethod()) {
		case DEPLOY_MECHANISM:
			result = _pdpHandler.deployMechanism(pmpRequest.getMechanism());
			break;
		case EXPORT_MECHANISM:
			result = _pdpHandler.exportMechanism(pmpRequest.getStringParameter());
			break;
		case REVOKE_MECHANISM:
			result = _pdpHandler.revokeMechanism(pmpRequest.getStringParameter());
			break;
		default:
			throw new RuntimeException("Method " + pmpRequest.getMethod()
					+ " is not supported!");
		}
		return result;
	}

	/**
	 * For handling remote PIP requests.
	 *
	 * @param pipRequest the request to process
	 * @return
	 */
	private Object processPipRequest(PipRequest pipRequest) {
		_logger.debug("Process PIP request " + pipRequest);

		Object result = null;

		switch (pipRequest.getMethod()) {
			case HAS_CONTAINER:
				// TODO Florian Kelbert.
				break;
			case HAS_DATA:
				// TODO Florian Kelbert.
				break;
			case NOTIFY_EVENT:
				result = _pipHandler.notifyActualEvent(pipRequest.getEvent());
				break;
		}

		return result;
	}


	private IStatus notifyEventToPip(IEvent event) {
		try {

			// TODO:
			// FIXME: need to restore this function. At the moment pip
			// communication hardcoded in pdp notify event
			// TODO:

			/****
			 *
			 * REMOTE PIP SOLUTION: original code from Alexander Stoimenov
			 *
			 */

			/*
			 * IPdp2PipFast pipProxy= getPdp2PipProxy(); // TODO maybe thes
			 * better solution will be // to establish the connection only once
			 * and keep // it alive
			 * _logger.debug("Establish connection to PIP"); pipProxy.connect();
			 * IStatus status = pipProxy.notifyActualEvent(event);
			 * pipProxy.disconnect(); return status;
			 */

			/***
			 *
			 * LOCAL PIP SOLUTION by Enrico Lovat
			 *
			 *
			 * The pip object and the respective cacher component will be
			 * created only if needed on the first invocation of this method.
			 *
			 * REQ_HANDLER ---------------------| | | V | PDP_CORE ----| V |
			 * |---> PIPCACHER --> PIP PDP_ENGINE---| (PDPjava)
			 *
			 * The current requestHandler is the PDP_CORE in the picture above
			 *
			 */

			return _pipHandler.notifyActualEvent(event);

			// / return _mf.createStatus(EStatus.OKAY);
		} catch (Exception e) {
			_logger.fatal("Failed to notify actual event to PIP.", e);
			return _mf.createStatus(EStatus.ERROR, "Error at PDP: " + e.getMessage());
		}
	}

	private IStatus delegeteUpdateIfFlowToPip(
			UpdateIfFlowSemanticsRequestWrapper request) {
		_logger.debug("Delegate update if flow to PIP");
		try {
			_logger.debug("Establish connection to PIP");

			_pdp2PipProxy = ConnectionManager.MAIN.obtain(_pdp2PipProxy);

			File jarFile = new File(FileUtils.getTempDirectory(), "jarFile"
					+ System.currentTimeMillis());
			FileUtils.writeByteArrayToFile(jarFile, request.getJarBytes());
			IStatus status = _pdp2PipProxy.updateInformationFlowSemantics(
					request.getPipDeployer(), jarFile,
					request.getConflictResolution());
			jarFile.delete();

			ConnectionManager.MAIN.release(_pdp2PipProxy);

			return status;
		} catch (Exception e) {
			_logger.fatal("Failed to notify actual event to PIP.", e);
			return _mf.createStatus(EStatus.ERROR, "Error at PDP: " + e.getMessage());
		}
	}

	private class RequestWrapper {
		// Forwarder is the thread that will send back the response
		private final IForwarder _forwarder;

		public RequestWrapper(IForwarder forwarder) {
			super();
			_forwarder = forwarder;
		}

		public IForwarder getForwarder() {
			return _forwarder;
		}

	}

	private class PmpRequestWrapper extends RequestWrapper {
		private final PmpRequest _pmpRequest;

		public PmpRequestWrapper(IForwarder forwarder, PmpRequest pmpRequest) {
			super(forwarder);
			_pmpRequest = pmpRequest;
		}

		@Override
		public String toString() {
			return "PmpRequestWrapper [_pmpRequest=" + _pmpRequest + "]";
		}

		public PmpRequest getPmpRequest() {
			return _pmpRequest;
		}
	}

	private class PepNotifyEventRequestWrapper extends RequestWrapper {
		private final IEvent _event;

		public PepNotifyEventRequestWrapper(IForwarder forwarder, IEvent event) {
			super(forwarder);
			_event = event;
		}

		@Override
		public String toString() {
			return "PepRequestWrapper [_event=" + _event + "]";
		}

		public IEvent getEvent() {
			return _event;
		}
	}


	/**
	 *
	 * @author Florian Kelbert
	 *
	 */
	private class PipRequestWrapper extends RequestWrapper {
		PipRequest _pipRequest;

		public PipRequestWrapper(IForwarder forwarder, PipRequest pipRequest) {
			super(forwarder);
			_pipRequest = pipRequest;
		}

		@Override
		public String toString() {
			return "PipRequestWrapper [_pipRequest=" + _pipRequest + "]";
		}

		public PipRequest getPipRequest() {
			return _pipRequest;
		}
	}


	/**
	 * Update information flow semantics request wrapper. Used for placing
	 * requests in the queue.
	 *
	 * @author Stoimenov
	 *
	 */
	private class UpdateIfFlowSemanticsRequestWrapper extends RequestWrapper {
		private IPipDeployer _pipDeployer;
		private byte[] _jarBytes;
		private EConflictResolution _conflictResolution;

		public UpdateIfFlowSemanticsRequestWrapper(IForwarder forwarder) {
			super(forwarder);
		}

		public IPipDeployer getPipDeployer() {
			return _pipDeployer;
		}

		public void setPipDeployer(IPipDeployer pipDeployer) {
			_pipDeployer = pipDeployer;
		}

		public byte[] getJarBytes() {
			return _jarBytes;
		}

		public void setJarBytes(byte[] jarBytes) {
			_jarBytes = jarBytes;
		}

		public EConflictResolution getConflictResolution() {
			return _conflictResolution;
		}

		public void setConflictResolution(EConflictResolution conflictResolution) {
			_conflictResolution = conflictResolution;
		}

	}
}
