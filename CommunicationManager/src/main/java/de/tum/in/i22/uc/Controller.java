package de.tum.in.i22.uc;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Any;
import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.i22.in.uc.thrift.types.TAny2Pip;
import de.tum.i22.in.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.cm.thrift.GenericThriftServer;
import de.tum.in.i22.uc.cm.thrift.TAny2AnyServerHandler;
import de.tum.in.i22.uc.cm.thrift.TAny2PdpServerHandler;
import de.tum.in.i22.uc.cm.thrift.TAny2PipServerHandler;
import de.tum.in.i22.uc.cm.thrift.TAny2PmpServerHandler;

public class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private static Settings _settings;

	private static GenericThriftServer _pdpServer;
	private static GenericThriftServer _pipServer;
	private static GenericThriftServer _pmpServer;
	private static GenericThriftServer _anyServer;

	public static void main(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);

		// load properties
		if (cl.hasOption(CommandLineOptions.OPTION_PDP_PROPS)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PDP_PROPS));
		}
		startUC();
	}

	static void startUC(){
		_settings = Settings.getInstance();

		new Thread(RequestHandler.getInstance()).start();

		startListeners();


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


	private static void startListeners() {
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

	public static boolean started() {
		return (!_settings.isPdpListenerEnabled() || _pdpServer.started())
				&& (!_settings.isPipListenerEnabled() || _pipServer.started())
				&& (!_settings.isPmpListenerEnabled() || _pmpServer.started())
				&& (!_settings.isAnyListenerEnabled() || _anyServer.started());
	}

}
