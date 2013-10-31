package de.tum.in.i22.pip;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.cm.in.pdp.PdpFastServiceHandler;
import de.tum.in.i22.pip.cm.in.pmp.Pmp2PipFastServiceHandler;
import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class PipController {
	private static Logger _logger = Logger.getLogger(PipController.class);
	
	private boolean _isStarted = false;
	
	public static void main(String[] args) {
		
		try {
			PipSettings.getInstance().loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}
		
		PipController pip = new PipController();
		
		pip.start();
		
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
		_logger.info("Start pip");
		
		// FIXME based on an argument, perform initialization of this
//		_logger.info("Initialize PipHandler");
//		PipHandler.getInstance();
				
		_logger.info("Start PdpFastServiceHandler");
		PipSettings settings = getPipSettings();
		int pdpListenerPortNum = settings.getPdpListenerPortNum();
		FastServiceHandler pdpFastServiceHandler = new PdpFastServiceHandler(pdpListenerPortNum);
		Thread threadPdpFastServiceHandler = new Thread(pdpFastServiceHandler);
		threadPdpFastServiceHandler.start();
		
		
		int pmpListenerPortNum = settings.getPmpListenerPortNum();
		FastServiceHandler pmpFastServiceHandler = new Pmp2PipFastServiceHandler(pmpListenerPortNum);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();
	}
	
	public PipSettings getPipSettings() {
		return PipSettings.getInstance();
	}
}
