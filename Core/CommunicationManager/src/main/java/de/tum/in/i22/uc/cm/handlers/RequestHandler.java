package de.tum.in.i22.uc.cm.handlers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.pdp.PdpRequest;
import de.tum.in.i22.uc.cm.requests.pip.PipRequest;
import de.tum.in.i22.uc.cm.server.IForwarder;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.cm.server.PipProcessor;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.cm.server.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;
import de.tum.in.i22.uc.pmp.requests.PmpRequest;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;


public class RequestHandler implements Runnable, IRequestHandler {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	private static RequestHandler _instance;

	private final Settings _settings;

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper> _requestQueue;

	private final PdpProcessor PDP;
	private final PipProcessor PIP;
	private final PmpProcessor PMP;

	private final Set<Integer> _portsUsed;

	private final ThriftClientHandlerFactory thriftClientFactory;

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
		thriftClientFactory = new ThriftClientHandlerFactory();

		/* Important: Creation of the handlers depends on properly initialized _portsUsed */
		PDP = createPdpHandler();
		PIP = createPipHandler();
		PMP = createPmpHandler();

		PDP.init(PIP, PMP);
		PIP.init(PDP, PMP);
		PMP.init(PIP, PDP);
	}

	private PdpProcessor createPdpHandler() {
		Location loc = _settings.getPdpLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PdpClientHandler pdp = thriftClientFactory.createPdpClientHandler(new IPLocation(iploc.getHost(), iploc.getPort()));
					try {
						pdp.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pdp.", e);
						throw new RuntimeException(e.getMessage(), e);

					}
					return pdp;
				}
				break;
			case LOCAL:
			default:
				return new PdpHandler();
		}

		return null;
	}

	private PmpProcessor createPmpHandler() {
		Location loc = _settings.getPmpLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PmpClientHandler pmp = thriftClientFactory.createPmpClientHandler(new IPLocation(iploc.getHost(), iploc.getPort()));
					try {
						pmp.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pmp.", e);
						throw new RuntimeException(e.getMessage(), e);
					}
					return pmp;
				}
				break;
			case LOCAL:
			default:
				return new PmpHandler();
		}

		return null;
	}

	private PipProcessor createPipHandler() {
		Location loc = _settings.getPipLocation();

		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PipClientHandler pip = thriftClientFactory.createPipClientHandler(new IPLocation(iploc.getHost(), iploc.getPort()));
					try {
						pip.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pip.", e);
						throw new RuntimeException(e.getMessage(), e);
					}
					return pip;
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

	@Override
	public void addRequest(Request<?,?> request, IForwarder forwarder) {
		_instance._requestQueue.add(new RequestWrapper(request, forwarder));
	}

	@Override
	public void run() {
		_logger.debug("Request handler run method");
		while (!Thread.interrupted()) {
			RequestWrapper requestWrapper = null;
			try {
				requestWrapper = _requestQueue.take();
			} catch (InterruptedException e) {
				_logger.error("Event handler interrupted.", e);
				return;
			}

			Request<?,?> request = requestWrapper.getRequest();
			IForwarder forwarder = requestWrapper.getForwarder();
			Object response = null;

			_logger.debug("Processing " + request);

			if (request instanceof PdpRequest) {
				response = ((PdpRequest<?>) request).process(PDP);
			} else if (request instanceof PipRequest) {
				response = ((PipRequest<?>) request).process(PIP);
			} else if (request instanceof PmpRequest) {
				response = ((PmpRequest<?>) request).process(PMP);
			} else {
				_logger.warn("Unknown queue element: " + request);
			}

			if (forwarder != null) {
				forwarder.forwardResponse(request, response);
			}
			_logger.debug("response forwarded");
		}

		// the thread is interrupted, stop processing the events
	}

	class RequestWrapper {
		private final IForwarder _forwarder;
		private final Request<?,?> _request;

		public RequestWrapper(Request<?,?> request, IForwarder forwarder) {
			_forwarder = forwarder;
			_request = request;
		}

		public IForwarder getForwarder() {
			return _forwarder;
		}

		public Request<?,?> getRequest() {
			return _request;
		}
	}


	public IAny2Pdp getPDP() {
		return PDP;
	}

	public IAny2Pip getPIP() {
		return PIP;
	}

	public IAny2Pmp getPMP() {
		return PMP;
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
