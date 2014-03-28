package de.tum.in.i22.uc.cm.in;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.TAny2Any;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;
import de.tum.in.i22.uc.cm.in.thrift.GenericThriftServer;
import de.tum.in.i22.uc.cm.in.thrift.TAny2AnyServerHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PdpServerHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PipServerHandler;
import de.tum.in.i22.uc.cm.in.thrift.TAny2PmpServerHandler;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.out.thrift.ThriftPdpClientHandler;
import de.tum.in.i22.uc.cm.out.thrift.ThriftPipClientHandler;
import de.tum.in.i22.uc.cm.out.thrift.ThriftPmpClientHandler;
import de.tum.in.i22.uc.cm.requests.PdpRequest;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.cm.requests.PmpRequest;
import de.tum.in.i22.uc.cm.requests.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.distribution.IPLocation;
import de.tum.in.i22.uc.distribution.Location;
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

	private final Set<Integer> _portsUsed;

	public static RequestHandler getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (RequestHandler.class) {
				if (_instance == null) _instance = new RequestHandler();
			}
		}
		return _instance;
	}

	private RequestHandler() {
		_requestQueue = new LinkedBlockingQueue<>();
		_settings = Settings.getInstance();
		_portsUsed = portsInUse();

		PDP = createPdpHandler();
		PIP = createPipHandler();
		PMP = createPmpHandler();

		PDP.init(PIP, PMP);
		PIP.init(PDP, PMP);
		PMP.init(PIP, PDP);

		startListeners();
	}

	private IAny2Pdp createPdpHandler() {
		Location loc = _settings.getPdpLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					return new ThriftPdpClientHandler(iploc.getHost(), iploc.getPort());
				}
				break;
			case LOCAL:
			default:
				return new PdpHandler();
		}

		return null;
	}

	private IAny2Pmp createPmpHandler() {
		Location loc = _settings.getPmpLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					return new ThriftPmpClientHandler(iploc.getHost(), iploc.getPort());
				}
				break;
			case LOCAL:
			default:
				return new PmpHandler();
		}

		return null;
	}

	private IAny2Pip createPipHandler() {
		Location loc = _settings.getPipLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					return new ThriftPipClientHandler(iploc.getHost(), iploc.getPort());
				}
				break;
			case LOCAL:
			default:
				return new PipHandler();
		}

		return null;
	}

	private Set<Integer> portsInUse() {
		Set<Integer> result = new HashSet<>();

		if (_settings.isPdpListenerEnabled()) {
			result.add(_settings.getPdpListenerPort());
		}

		if (_settings.isPipListenerEnabled()) {
			result.add(_settings.getPipListenerPort());
		}

		if (_settings.isPmpListenerEnabled()) {
			result.add(_settings.getPmpListenerPort());
		}

		if (_settings.isAnyListenerEnabled()) {
			result.add(_settings.getAnyListenerPort());
		}

		return result;
	}

	private boolean isConnectionAllowed(IPLocation loc) {
		RuntimeException rte = new RuntimeException("Not allowed to forward PIP/PMP/PDP requests to " + loc + ". "
				+ "Rethink your setup/configuration.");

		InetAddress addr;

		try {
			addr = InetAddress.getByName(loc.getHost());
		} catch (UnknownHostException e) {
			throw rte;
		}

		if ((addr.isAnyLocalAddress() || addr.isLoopbackAddress()) && _portsUsed.contains(loc.getPort())) {
			throw rte;
		}

		return true;
	}


	private void startListeners() {
		if (_settings.isPdpListenerEnabled()) {
			_pdpServer = new GenericThriftServer(
								_settings.getPdpListenerPort(),
								new TAny2Pdp.Processor<TAny2PdpServerHandler>(new TAny2PdpServerHandler(PDP)));
			new Thread(_pdpServer).start();
		}

		if (_settings.isPipListenerEnabled()) {
			_pipServer = new GenericThriftServer(
								_settings.getPipListenerPort(),
								new TAny2Pip.Processor<TAny2PipServerHandler>(new TAny2PipServerHandler(PIP)));
			new Thread(_pipServer).start();
		}

		if (_settings.isPmpListenerEnabled()) {
			_pmpServer = new GenericThriftServer(
								_settings.getPmpListenerPort(),
								new TAny2Pmp.Processor<TAny2PmpServerHandler>(new TAny2PmpServerHandler(PMP)));
			new Thread(_pmpServer).start();
		}

		if (_settings.isAnyListenerEnabled()) {
			_anyServer = new GenericThriftServer(
								_settings.getAnyListenerPort(),
								new TAny2Any.Processor<TAny2AnyServerHandler>(new TAny2AnyServerHandler()));
			new Thread(_anyServer).start();
		}
	}

	public <T extends Request> void addRequest(T request, IForwarder forwarder) {
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

			// TODO double check this forwarding. Has never been checked in the simplified branch
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

	class RequestWrapper<R extends Request> {
		private final IForwarder _forwarder;
		private final R _request;

		public RequestWrapper(R request, IForwarder forwarder) {
			_forwarder = forwarder;
			_request = request;
		}

		public IForwarder getForwarder() {
			return _forwarder;
		}

		public R getRequest() {
			return _request;
		}
	}


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

}
