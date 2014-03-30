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
import de.tum.in.i22.uc.cm.out.thrift.ThriftPdpClientHandler;
import de.tum.in.i22.uc.cm.out.thrift.ThriftPipClientHandler;
import de.tum.in.i22.uc.cm.out.thrift.ThriftPmpClientHandler;
import de.tum.in.i22.uc.cm.processing.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.distribution.IPLocation;
import de.tum.in.i22.uc.distribution.Location;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pdp.PdpProcessor;
import de.tum.in.i22.uc.pdp.requests.PdpRequest;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pip.PipProcessor;
import de.tum.in.i22.uc.pip.requests.PipRequest;
import de.tum.in.i22.uc.pmp.PmpHandler;
import de.tum.in.i22.uc.pmp.PmpProcessor;
import de.tum.in.i22.uc.pmp.requests.PmpRequest;

public class RequestHandler implements Runnable {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	private static RequestHandler _instance;

	private final Settings _settings;

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper> _requestQueue;

	private final PdpProcessor PDP;
	private final PipProcessor PIP;
	private final PmpProcessor PMP;

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

		/* Important: Creation of the handlers depends on properly initialized _portsUsed */
		PDP = createPdpHandler();
		PIP = createPipHandler();
		PMP = createPmpHandler();

		PDP.init(PIP, PMP);
		PIP.init(PDP, PMP);
		PMP.init(PIP, PDP);

		startListeners();
	}

	private PdpProcessor createPdpHandler() {
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

	private PmpProcessor createPmpHandler() {
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

	private PipProcessor createPipHandler() {
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
								new TAny2Pdp.Processor<TAny2PdpServerHandler>(new TAny2PdpServerHandler()));
			new Thread(_pdpServer).start();
		}

		if (_settings.isPipListenerEnabled()) {
			_pipServer = new GenericThriftServer(
								_settings.getPipListenerPort(),
								new TAny2Pip.Processor<TAny2PipServerHandler>(new TAny2PipServerHandler()));
			new Thread(_pipServer).start();
		}

		if (_settings.isPmpListenerEnabled()) {
			_pmpServer = new GenericThriftServer(
								_settings.getPmpListenerPort(),
								new TAny2Pmp.Processor<TAny2PmpServerHandler>(new TAny2PmpServerHandler()));
			new Thread(_pmpServer).start();
		}

		if (_settings.isAnyListenerEnabled()) {
			_anyServer = new GenericThriftServer(
								_settings.getAnyListenerPort(),
								new TAny2Any.Processor<TAny2AnyServerHandler>(new TAny2AnyServerHandler()));
			new Thread(_anyServer).start();
		}
	}

	public void addRequest(Request<?,?> request, Forwarder forwarder) {
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
			Forwarder forwarder = requestWrapper.getForwarder();
			Object response = null;

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

	class RequestWrapper {
		private final Forwarder _forwarder;
		private final Request<?,?> _request;

		public RequestWrapper(Request<?,?> request, Forwarder forwarder) {
			_forwarder = forwarder;
			_request = request;
		}

		public Forwarder getForwarder() {
			return _forwarder;
		}

		public Request<?,?> getRequest() {
			return _request;
		}
	}
}
