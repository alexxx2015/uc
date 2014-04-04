package de.tum.in.i22.uc;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftProcessorFactory;

public class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private static Settings _settings;

	private static IThriftServer _pdpServer;
	private static IThriftServer _pipServer;
	private static IThriftServer _pmpServer;
	private static IThriftServer _anyServer;

	public static void main(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);

		// load properties
		if (cl.hasOption(CommandLineOptions.OPTION_PROPFILE)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PROPFILE));
		}
		startUC();

		/*
		 * Lock forever
		 */
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static void startUC(){
		_settings = Settings.getInstance();

		RequestHandler requestHandler = RequestHandler.getInstance();

		new Thread(requestHandler).start();

		startListeners(requestHandler);
	}


	private static void startListeners(RequestHandler requestHandler) {
		if (_settings.isPdpListenerEnabled()) {
			_pdpServer = ThriftProcessorFactory.createPdpThriftServer(_settings.getPdpListenerPort(), requestHandler);

			if (_pdpServer != null) {
				new Thread(_pdpServer).start();
			}
		}


		if (_settings.isPipListenerEnabled()) {
			_pipServer = ThriftProcessorFactory.createPipThriftServer(_settings.getPipListenerPort(), requestHandler);

			if (_pipServer != null) {
				new Thread(_pipServer).start();
			}
		}

		if (_settings.isPmpListenerEnabled()) {
			_pmpServer = ThriftProcessorFactory.createPmpThriftServer(_settings.getPmpListenerPort(), requestHandler);

			if (_pmpServer != null) {
				new Thread(_pmpServer).start();
			}
		}

		if (_settings.isAnyListenerEnabled()) {
			_anyServer = ThriftProcessorFactory.createAnyThriftServer(_settings.getAnyListenerPort(), requestHandler);

			if (_anyServer != null) {
				new Thread(_anyServer).start();
			}
		}
	}

	public static boolean started() {
		return (!_settings.isPdpListenerEnabled() || (_pdpServer != null && _pdpServer.started()))
				&& (!_settings.isPipListenerEnabled() || (_pipServer != null && _pipServer.started()))
				&& (!_settings.isPmpListenerEnabled() || (_pmpServer != null && _pmpServer.started()))
				&& (!_settings.isAnyListenerEnabled() || (_anyServer != null && _anyServer.started()));
	}

}
