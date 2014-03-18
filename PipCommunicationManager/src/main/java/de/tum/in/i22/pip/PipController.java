package de.tum.in.i22.pip;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.cm.in.pdp.PdpTcpServiceHandler;
import de.tum.in.i22.pip.cm.in.pmp.Pmp2PipTcpServiceHandler;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.in.TcpServiceHandler;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.settings.PipSettings;

public class PipController {
	private static Logger _logger = Logger.getLogger(PipController.class);

	private boolean _isStarted = false;

	private static PipSettings _pipSettings = PipSettings.getInstance();

	private final IPdp2Pip _pipHandler;

	public PipController(IPdp2Pip pipHandler) {
		_pipHandler = pipHandler;
	}

	public static void main(String[] args) {

		try {
			_pipSettings.loadProperties();
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.", e);
			return;
		}

		new PipController(new PipHandler(_pipSettings.getPipRemotePortNum())).start();

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
		if (_isStarted) {
			return;
		}

		_logger.info("Start pip");

		int pdpListenerPortNum = _pipSettings.getPdpListenerPortNum();
		TcpServiceHandler pdpFastServiceHandler = new PdpTcpServiceHandler(pdpListenerPortNum, _pipHandler);
		Thread threadPdpFastServiceHandler = new Thread(pdpFastServiceHandler);
		threadPdpFastServiceHandler.start();

		int pmpListenerPortNum = _pipSettings.getPmpListenerPortNum();
		TcpServiceHandler pmpFastServiceHandler = new Pmp2PipTcpServiceHandler(pmpListenerPortNum);
		Thread threadPmpFastServiceHandler = new Thread(pmpFastServiceHandler);
		threadPmpFastServiceHandler.start();

		_isStarted = true;
	}
}
