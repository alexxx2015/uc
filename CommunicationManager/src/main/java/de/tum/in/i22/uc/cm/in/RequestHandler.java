package de.tum.in.i22.uc.cm.in;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.TAny2Any;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;
import de.tum.in.i22.uc.cm.in.thrift.GenericThriftServer;
import de.tum.in.i22.uc.cm.in.thrift.TAny2AnyHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PdpHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PipHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PmpHandler;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PdpRequest;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.cm.requests.PmpRequest;
import de.tum.in.i22.uc.cm.requests.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.distribution.ELocation;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.core.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class RequestHandler implements Runnable {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	private static RequestHandler _instance;

	private final Settings _settings;

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper<? extends Request>> _requestQueue;

	private final IAny2Pdp PDP;
	private final IAny2Pip PIP;
	private final IAny2Pmp PMP;

	private GenericThriftServer _pdpServer;
	private GenericThriftServer _pipServer;
	private GenericThriftServer _pmpServer;
	private GenericThriftServer _anyServer;

	public static synchronized RequestHandler getInstance() {
		if (_instance == null) {
			_instance = new RequestHandler();
		}
		return _instance;
	}

	private RequestHandler() {
		_requestQueue = new LinkedBlockingQueue<>();
		_settings = Settings.getInstance();

		PDP = createPdpHandler();
		PIP = createPipHandler();
		PMP = createPmpHandler();

		PDP.init(PIP, PMP);
		PIP.init(PDP, PMP);
		PMP.init(PIP, PDP);

		startListeners();
	}

	private IAny2Pdp createPdpHandler() {
		if (_settings.getPdpLocation().getLocation() == ELocation.LOCAL) {
			return PdpHandler.getInstance();
		}

		// TODO: Handle case of remote PDP
		return null;
	}

	private IAny2Pmp createPmpHandler() {
		if (_settings.getPmpLocation().getLocation() == ELocation.LOCAL) {
			return PmpHandler.getInstance();
		}

		// TODO: Handle case of remote PMP
		return null;
	}

	private IAny2Pip createPipHandler() {
		if (_settings.getPipLocation().getLocation() == ELocation.LOCAL) {
			return PipHandler.getInstance();
		}

		// TODO: Handle case of remote PIP
		return null;
	}


	private void startListeners() {
		_logger.debug("Starting listeners");

		int portPdp = _settings.getPdpListenerPort();
		int portPip = _settings.getPipListenerPort();
		int portPmp = _settings.getPmpListenerPort();
		int portAny = _settings.getAnyListenerPort();

		if (_settings.isPdpListenerEnabled()) {
			_pdpServer = new GenericThriftServer(portPdp, new TAny2Pdp.Processor<TAny2PdpHandler>(new TAny2PdpHandler(portPdp)));
			new Thread(_pdpServer).start();
		}

		if (_settings.isPipListenerEnabled()) {
			_pipServer = new GenericThriftServer(portPip, new TAny2Pip.Processor<TAny2PipHandler>(new TAny2PipHandler(portPip)));
			new Thread(_pipServer).start();
		}

		if (_settings.isPmpListenerEnabled()) {
			_pmpServer = new GenericThriftServer(portPmp, new TAny2Pmp.Processor<TAny2PmpHandler>(new TAny2PmpHandler(portPmp)));
			new Thread(_pmpServer).start();
		}

		if (_settings.isAnyListenerEnabled()) {
			_anyServer = new GenericThriftServer(portAny, new TAny2Any.Processor<TAny2AnyHandler>(new TAny2AnyHandler(portAny)));
			new Thread(_anyServer).start();
		}
	}

	public static <T extends Request> void addRequest(T request, IForwarder forwarder) {
		_instance._requestQueue.add(new RequestWrapper<T>(request, forwarder));
	}

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

	public boolean started() {
		return (!_settings.isPdpListenerEnabled() || _pdpServer.started())
				&& (!_settings.isPipListenerEnabled() || _pipServer.started())
				&& (!_settings.isPmpListenerEnabled() || _pmpServer.started())
				&& (!_settings.isAnyListenerEnabled() || _anyServer.started());
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
