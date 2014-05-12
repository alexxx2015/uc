package de.tum.in.i22.uc.cm.handlers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.processing.IForwarder;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.Processor;
import de.tum.in.i22.uc.cm.processing.Request;
import de.tum.in.i22.uc.pdp.requests.PdpRequest;
import de.tum.in.i22.uc.pip.requests.PipRequest;
import de.tum.in.i22.uc.pmp.requests.PmpRequest;

class RequestQueueManager implements Runnable {

	private static Logger _logger = LoggerFactory.getLogger(RequestQueueManager.class);

	// Do _NOT_ use an ArrayBlockingQueue. It swallowed up 2/3 of all requests added to the queue
	// when using JNI and dispatching _many_ events. This took me 5 hours of debugging! -FK-
	private final BlockingQueue<RequestWrapper> _requestQueue;

	private final PdpProcessor _pdp;
	private final PipProcessor _pip;
	private final PmpProcessor _pmp;

	private boolean run = true;

	RequestQueueManager(PdpProcessor pdp, PipProcessor pip, PmpProcessor pmp) {
		_requestQueue = new LinkedBlockingQueue<>();

		_pdp = pdp;
		_pip = pip;
		_pmp = pmp;
	}


	void addRequest(Request<?,?> request, IForwarder forwarder) {
		_logger.info("Enqueuing " + request);
		_requestQueue.add(new RequestWrapper(request, forwarder));
	}

	@Override
	public void run() {
		_logger.debug("Request handler run method");

		while (!Thread.interrupted() && run ) {
			RequestWrapper requestWrapper = null;
			try {
				requestWrapper = _requestQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				run=false;
				break;
			}

			Request<?,?> request = requestWrapper.getRequest();
			IForwarder forwarder = requestWrapper.getForwarder();
			Object response = null;

			_logger.debug("Processing " + request);

			if (request instanceof PdpRequest) {
				response = ((PdpRequest<?>) request).process(_pdp);
			} else if (request instanceof PipRequest) {
				response = ((PipRequest<?>) request).process(_pip);
			} else if (request instanceof PmpRequest) {
				response = ((PmpRequest<?>) request).process(_pmp);
			} else if (request instanceof PoisonPillRequest) {
				response = ((PoisonPillRequest)request).process(new PoisonPillProcessor());
			} else {
				_logger.warn("Unknown queue element: " + request);
			}
			if (forwarder != null) {
				forwarder.forwardResponse(request, response);
			}
		}

		// the thread is interrupted, stop processing the events
	}

	private class RequestWrapper {
		private final IForwarder _forwarder;
		private final Request<?,?> _request;

		RequestWrapper(Request<?,?> request, IForwarder forwarder) {
			_forwarder = forwarder;
			_request = request;
		}

		IForwarder getForwarder() {
			return _forwarder;
		}

		Request<?,?> getRequest() {
			return _request;
		}
	}

	public void stop() {
		_requestQueue.add(new RequestWrapper(new PoisonPillRequest(), null));
	}

	private class PoisonPillProcessor extends Processor<PoisonPillProcessor, PoisonPillProcessor>{

		public PoisonPillProcessor() {
			super(null);
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}

	}
	private class PoisonPillRequest extends Request<Object, PoisonPillProcessor>{

		@Override
		public Object process(PoisonPillProcessor processor) {
			run=false;
			return null;
		}

	}
}
