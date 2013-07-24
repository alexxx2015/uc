package de.tum.in.i22.pip;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.cm.in.FastServiceHandler;
import de.tum.in.i22.pip.cm.in.pdp.PdpFastServiceHandler;

public class PipController {
	private static Logger _logger = Logger.getLogger(PipController.class);
	
	private boolean _isStarted = false;
	
	public static void main(String[] args) {
		
		try {
			PipSettings.loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}
		
		PipController pip = new PipController();
		
		pip.start(PipSettings.getPdpListenerPortNum());
		
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
	
	public void start(int pdpListenerPortNum) {
		if (_isStarted)
			return;
		_isStarted = true;
		
		_logger.info("Start pip");
		
		_logger.info("Start PdpFastServiceHandler");
		FastServiceHandler pdpFastServiceHandler = new PdpFastServiceHandler(pdpListenerPortNum);
		Thread threadPdpFastServiceHandler = new Thread(pdpFastServiceHandler);
		threadPdpFastServiceHandler.start();
	}
}
