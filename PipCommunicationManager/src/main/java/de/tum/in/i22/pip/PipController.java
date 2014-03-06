package de.tum.in.i22.pip;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.tum.in.i22.pip.cm.in.pdp.PdpTcpServiceHandler;
import de.tum.in.i22.pip.cm.in.pmp.Pmp2PipTcpServiceHandler;
import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.injection.PipModule;
import de.tum.in.i22.uc.cm.in.TcpServiceHandler;

public class PipController {
	private static Logger _logger = Logger.getLogger(PipController.class);
	
	private boolean _isStarted = false;
	
	private IPdp2Pip _pipHandler;
	
	@Inject
	public PipController(IPdp2Pip pipHandler) {
		_pipHandler = pipHandler;
	}
	
	public static void main(String[] args) {
		
		try {
			PipSettings.getInstance().loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}
		
		Injector injector = Guice.createInjector(new PipModule());
		
		PipController pip = injector.getInstance(PipController.class);
		
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
		
		_logger.info("Start PdpFastServiceHandler");
		PipSettings settings = getPipSettings();
		int pdpListenerPortNum = settings.getPdpListenerPortNum();
		
		TcpServiceHandler pdpFastServiceHandler = new PdpTcpServiceHandler(pdpListenerPortNum, _pipHandler);
		Thread threadPdpFastServiceHandler = new Thread(pdpFastServiceHandler);
		threadPdpFastServiceHandler.start();
		
		
		int pmpListenerPortNum = settings.getPmpListenerPortNum();
		TcpServiceHandler pmpFastServiceHandler = new Pmp2PipTcpServiceHandler(pmpListenerPortNum);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();
	}
	
	public PipSettings getPipSettings() {
		return PipSettings.getInstance();
	}
}
