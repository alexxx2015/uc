package de.tum.in.i22.pdp;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpFastServiceHandler;
import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class PdpController {
	
	private static Logger _logger = Logger.getLogger(PdpController.class);
	
	private boolean _isStarted = false;
	
	public static void main(String[] args) {
		try {
			PdpSettings.getInstance().loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}
		
		PdpController pdp = new PdpController();
		pdp.start();
		
//		 EventHandler thread loops forever, this stops the main thread,
//		 otherwise the app will be closed
		Object lock = new Object();
		synchronized(lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				_logger.error("EventHandler thread interrupted.", e);
			}
		}
	}
	
	public void start() {
		if (_isStarted)
			return;
		_isStarted = true;
		
		_logger.info("Start pdp");
		
		_logger.info("Start Request Handler");
		RequestHandler eventHandler = RequestHandler.getInstance();
		Thread thread = new Thread(eventHandler);
		thread.start();

		int pmpListenerPort = getPdpSettings().getPmpListenerPortNum();
		_logger.info("Start PmpFastServiceHandler on port: " + pmpListenerPort);
		FastServiceHandler pmpFastServiceHandler = new PmpFastServiceHandler(pmpListenerPort);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();
		
		int pepListenerPort = getPdpSettings().getPepListenerPortNum();
		_logger.info("Start PepFastServiceHandler on port: " + pepListenerPort);
		FastServiceHandler pepFastServiceHandler = new PepFastServiceHandler(pepListenerPort);
		Thread threadPepFastServiceHandler = new Thread(pepFastServiceHandler);
		threadPepFastServiceHandler.start();
	}
	
	public PdpSettings getPdpSettings() {
		return PdpSettings.getInstance();
	}
}
