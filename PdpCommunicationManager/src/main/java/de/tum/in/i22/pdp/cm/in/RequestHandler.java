package de.tum.in.i22.pdp.cm.in;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.tum.in.i22.cm.pdp.PolicyDecisionPoint;
import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pdp.cm.in.pmp.PmpRequest;
import de.tum.in.i22.pdp.cm.out.pip.IPdp2PipFast;
import de.tum.in.i22.pdp.cm.out.pip.Pdp2PipImp;
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

public class RequestHandler implements Runnable {
	private static Logger _logger = Logger.getRootLogger();
	private static RequestHandler _instance = null;
	private BlockingQueue<RequestWrapper> _requestQueue = null;

	private IIncoming pdpHandler;
	private IPdp2PipFast _pdp2PipProxy = null;

	private static IPdpCore2PipCacher _core2pip = null;
	private static IPdpEngine2PipCacher _engine2pip = null;
	private static IPipCacher2Pip _pipHandler = null;
	
	public IPdpCore2PipCacher getCore2Pip(){
		return _core2pip ;
	}
	public IPipCacher2Pip getPipHandler(){
		return _pipHandler;
	}

	public void initializeAll() {
		// 'misuse' the PIP's port as an ID for the PIP's database. That's fine.
		_pipHandler = new PipHandler(PdpSettings.getInstance().getPipPortNum());

		// 1 implementation for 2 interfaces
		_core2pip = new PipCacherImpl(_pipHandler);
		_engine2pip = (PipCacherImpl) _core2pip;
		populate();
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

	private IMessageFactory _mf = MessageFactoryCreator.createMessageFactory();

	public static RequestHandler getInstance() {
		if (_instance == null) {
			_instance = new RequestHandler();
		}
		return _instance;
	}

	public void setPdpHandler(IIncoming pdpHandler) {
		this.pdpHandler = pdpHandler;
		initializeAll();
		this.pdpHandler.setPdpCore2PipCacher(_core2pip);
		this.pdpHandler.setPdpEngine2PipCacher(_engine2pip);
	}

	private RequestHandler() {
		int queueSize = PdpSettings.getInstance().getQueueSize();
		_requestQueue = new ArrayBlockingQueue<RequestWrapper>(queueSize, true);
	}

	public void addEvent(IEvent event, IForwarder forwarder)
			throws InterruptedException {
		// add event to the tail of the queue
		// put method blocks until the space in the queue becomes available
		RequestWrapper obj = new PepNotifyEventRequestWrapper(forwarder, event);
		_logger.debug("Add " + obj + " to the queue.");
		_requestQueue.put(obj);
	}

	public void addPmpRequest(PmpRequest request, IForwarder forwarder)
			throws InterruptedException {
		// add pmpRequest to the tail of the queue
		// put method blocks until the space in the queue becomes available
		RequestWrapper obj = new PmpRequestWrapper(forwarder, request);
		_logger.debug("Add " + obj + "  to the queue.");
		_requestQueue.put(obj);
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

		_logger.debug("Add " + request + " to the queue.");
		_requestQueue.put(request);
	}

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
				IEvent event = ((PepNotifyEventRequestWrapper) request)
						.getEvent();
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
				
				response = pdpHandler.notifyEvent(event);
			} else if (request instanceof PmpRequestWrapper) {
				PmpRequest pmpRequest = ((PmpRequestWrapper) request)
						.getPmpRequest();
				response = processPmpRequest(pmpRequest);
			} else if (request instanceof UpdateIfFlowSemanticsRequestWrapper) {
				UpdateIfFlowSemanticsRequestWrapper updateIfFlowRequest = (UpdateIfFlowSemanticsRequestWrapper) request;
				response = delegeteUpdateIfFlowToPip(updateIfFlowRequest);
			} else {
				throw new RuntimeException("Queue element " + request
						+ " must be either event or PmpRequest!");
			}

			_logger.trace("event " + request.toString()
					+ " processed. forward response");
			IForwarder forwarder = request.getForwarder();
			forwarder.forwardResponse(response);
			_logger.trace("response forwarded");

		}

		// the thread is interrupted, stop processing the events
	}

	private Object processPmpRequest(PmpRequest pmpRequest) {
		_logger.debug("Process event " + pmpRequest);
		Object result = null;
		switch (pmpRequest.getMethod()) {
		case DEPLOY_MECHANISM:
			result = pdpHandler.deployMechanism(pmpRequest.getMechanism());
			break;
		case EXPORT_MECHANISM:
			result = pdpHandler
					.exportMechanism(pmpRequest.getStringParameter());
			break;
		case REVOKE_MECHANISM:
			result = pdpHandler
					.revokeMechanism(pmpRequest.getStringParameter());
			break;
		default:
			throw new RuntimeException("Method " + pmpRequest.getMethod()
					+ " is not supported!");
		}
		return result;
	}

	/**
	 * The proxy object will be created only if needed on the first invocation
	 * of this method.
	 * 
	 * @return
	 */
	private IPdp2PipFast getPdp2PipProxy() throws Exception {
		if (_pdp2PipProxy == null) {
			String pipAddress = PdpSettings.getInstance().getPipAddress();
			int pipPort = PdpSettings.getInstance().getPipPortNum();

			_logger.debug("Create proxy object to connect to PIP listener "
					+ pipAddress + ":" + pipPort);
			_pdp2PipProxy = new Pdp2PipImp(pipAddress, pipPort);
		}
		return _pdp2PipProxy;
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

			if (_pipHandler == null)
				initializeAll();

			IStatus status = _pipHandler.notifyActualEvent(event);
			return status;

			// / return _mf.createStatus(EStatus.OKAY);
		} catch (Exception e) {
			_logger.fatal("Failed to notify actual event to PIP.", e);
			return _mf.createStatus(EStatus.ERROR,
					"Error at PDP: " + e.getMessage());
		}
	}

	private IStatus delegeteUpdateIfFlowToPip(
			UpdateIfFlowSemanticsRequestWrapper request) {
		_logger.debug("Delegate update if flow to PIP");
		try {
			IPdp2PipFast pipProxy = getPdp2PipProxy();
			_logger.debug("Establish connection to PIP");
			pipProxy.connect();
			File jarFile = new File(FileUtils.getTempDirectory(), "jarFile"
					+ System.currentTimeMillis());
			FileUtils.writeByteArrayToFile(jarFile, request.getJarBytes());
			IStatus status = pipProxy.updateInformationFlowSemantics(
					request.getPipDeployer(), jarFile,
					request.getConflictResolution());
			jarFile.delete();
			pipProxy.disconnect();
			return status;
		} catch (Exception e) {
			_logger.fatal("Failed to notify actual event to PIP.", e);
			return _mf.createStatus(EStatus.ERROR,
					"Error at PDP: " + e.getMessage());
		}
	}

	private class RequestWrapper {
		// Forwarder is the thread that will send back the response
		private IForwarder _forwarder;

		public RequestWrapper(IForwarder forwarder) {
			super();
			_forwarder = forwarder;
		}

		public IForwarder getForwarder() {
			return _forwarder;
		}

	}

	private class PmpRequestWrapper extends RequestWrapper {
		private PmpRequest _pmpRequest;

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
		private IEvent _event;

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
