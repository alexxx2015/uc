package de.tum.in.i22.pdp;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.cm.in.FastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpFastServiceHandler;

public class PdpController {
	
	private static Logger _logger = Logger.getLogger(PdpController.class);
	
	private boolean _isStarted = false;
	
	public static void main(String[] args) {
		PdpController pdp = new PdpController();
		pdp.start(50001, 50002);
	}
	
	//TODO add additional parameters
	public void start(int portNumPep, int portNumPmp) {
		if (_isStarted)
			return;
		_isStarted = true;
		
		_logger.info("Start pdp");
		
		_logger.info("Start Request Handler");
		RequestHandler eventHandler = RequestHandler.getInstance();
		Thread thread = new Thread(eventHandler);
		thread.start();

		_logger.info("Start PmpFastServiceHandler");
		FastServiceHandler pmpFastServiceHandler = new PmpFastServiceHandler(portNumPmp);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();
		
		_logger.info("Start PepFastServiceHandler");
		FastServiceHandler pepFastServiceHandler = new PepFastServiceHandler(portNumPep);
		Thread threadPepFastServiceHandler = new Thread(pepFastServiceHandler);
		threadPepFastServiceHandler.start();

		
		// EventHandler thread loops forever, this stops the main thread,
		// otherwise the app will be closed
//		try {
//			thread.join();
//		} catch (InterruptedException e) {
//			_logger.error("EventHandler thread interrupted.", e);
//		}
	}
}
