package de.tum.in.i22.pdp;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.FastServiceHandler;

public class PdpController {
	
	private static Logger _logger = Logger.getRootLogger();
	
	private boolean _isStarted = false;
	
	public static void main(String[] args) {
		PdpController pdp = new PdpController();
		pdp.start(50001);
	}
	
	//TODO add additional parameters
	public void start(int portNum) {
		if (_isStarted)
			return;
		_isStarted = true;
		
		_logger.info("Start pdp.");
		
		_logger.info("Start EventHandler.");
		EventHandler eventHandler = EventHandler.getInstance();
		Thread thread = new Thread(eventHandler);
		thread.start();
		
		_logger.info("Start FastServiceHandler.");
		FastServiceHandler fastServiceHandler = new FastServiceHandler(portNum);
		Thread threadFastServiceHandler = new Thread(fastServiceHandler);
		threadFastServiceHandler.start();
		
		// EventHandler thread loops forever, this stops the main thread,
		// otherwise the app will be closed
//		try {
//			thread.join();
//		} catch (InterruptedException e) {
//			_logger.error("EventHandler thread interrupted.", e);
//		}
	}
}
