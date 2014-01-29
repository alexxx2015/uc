package de.tum.in.i22.pdp;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pep.thrift.ThriftServer;
import de.tum.in.i22.pdp.cm.in.pmp.PmpFastServiceHandler;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.injection.PdpModule;
import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class PdpController {

	private static Logger _logger = Logger.getLogger(PdpController.class);

	private boolean _isStarted = false;

	private IIncoming _pdpHandler;

	/**
	 * Use dependency injection to inject pdpHandler.
	 * 
	 * @param pdpHandler
	 */
	@Inject
	public PdpController(IIncoming pdpHandler) {
		_pdpHandler = pdpHandler;
	}

	public void start() {
		if (_isStarted)
			return;
		_isStarted = true;

		_logger.info("Start pdp");

		_logger.info("Start Request Handler");
		RequestHandler eventHandler = RequestHandler.getInstance();
		// PDP handler is injected when PdpController is created
		eventHandler.setPdpHandler(_pdpHandler);
		Thread thread = new Thread(eventHandler);
		thread.start();

		int pmpListenerPort = getPdpSettings().getPmpListenerPortNum();
		_logger.info("Start PmpFastServiceHandler on port: " + pmpListenerPort);
		FastServiceHandler pmpFastServiceHandler = new PmpFastServiceHandler(pmpListenerPort);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();

		int pepGPBListenerPort = getPdpSettings().getPepGPBListenerPortNum();
		_logger.info("Start PepGPBFastServiceHandler on port: " + pepGPBListenerPort);
		FastServiceHandler pepGPBFastServiceHandler = new PepFastServiceHandler(pepGPBListenerPort);
		Thread threadPepGPBFastServiceHandler = new Thread(pepGPBFastServiceHandler);
		threadPepGPBFastServiceHandler.start();
		
		int pepThriftListenerPort = getPdpSettings().getPepThriftListenerPortNum();
		_logger.info("Start PepThriftFastServiceHandler on port: " + pepThriftListenerPort);
		ThriftServer.createListener(pepThriftListenerPort, pepGPBListenerPort);
		ThriftServer.startListener();
	}

	public PdpSettings getPdpSettings() {
		return PdpSettings.getInstance();
	}

	public static void main(String[] args) {
		try {
			PdpSettings.getInstance().loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}

		/*
		 * Guice.createInjector() takes your Modules, and returns a new Injector
		 * instance. Most applications will call this method exactly once, in
		 * their main() method.
		 */
		Injector injector = Guice.createInjector(new PdpModule());

		/*
		 * Now that we've got the injector, we can build objects.
		 */
		PdpController pdp = injector.getInstance(PdpController.class);
		pdp.start();

		// EventHandler thread loops forever, this stops the main thread,
		// otherwise the app will be closed
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				_logger.error("EventHandler thread interrupted.", e);
			}
		}
	}
}
