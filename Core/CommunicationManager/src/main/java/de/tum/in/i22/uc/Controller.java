package de.tum.in.i22.uc;

import org.apache.commons.cli.CommandLine;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Any;
import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.i22.in.uc.thrift.types.TAny2Pip;
import de.tum.i22.in.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.cm.thrift.TAny2AnyThriftProcessor;
import de.tum.in.i22.uc.cm.thrift.TAny2PdpThriftProcessor;
import de.tum.in.i22.uc.cm.thrift.TAny2PipThriftProcessor;
import de.tum.in.i22.uc.cm.thrift.TAny2PmpThriftProcessor;
import de.tum.in.i22.uc.thrift.server.ThriftServer;

public class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private static Settings _settings;

	private static ThriftServer _pdpServer;
	private static ThriftServer _pipServer;
	private static ThriftServer _pmpServer;
	private static ThriftServer _anyServer;

	public static void main(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);

		// load properties
		if (cl.hasOption(CommandLineOptions.OPTION_PDP_PROPS)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PDP_PROPS));
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

		new Thread(RequestHandler.getInstance()).start();

		startListeners();
	}


	private static void startListeners() {
		if (_settings.isPdpListenerEnabled()) {
			try {
				_pdpServer = new ThriftServer(
									_settings.getPdpListenerPort(),
									new TAny2Pdp.Processor<TAny2PdpThriftProcessor>(new TAny2PdpThriftProcessor()));
			} catch (TTransportException e) {
				_logger.warn("Unable to start PDP server: " + e);
				_pdpServer = null;
			}

			if (_pdpServer != null) {
				new Thread(_pdpServer).start();
			}
		}


		if (_settings.isPipListenerEnabled()) {
			try {
				_pipServer = new ThriftServer(
									_settings.getPipListenerPort(),
									new TAny2Pip.Processor<TAny2PipThriftProcessor>(new TAny2PipThriftProcessor()));
			} catch (TTransportException e) {
				_logger.warn("Unable to start PIP server: " + e);
				_pipServer = null;
			}

			if (_pipServer != null) {
				new Thread(_pipServer).start();
			}
		}

		if (_settings.isPmpListenerEnabled()) {
			try {
				_pmpServer = new ThriftServer(
									_settings.getPmpListenerPort(),
									new TAny2Pmp.Processor<TAny2PmpThriftProcessor>(new TAny2PmpThriftProcessor()));
			} catch (TTransportException e) {
				_logger.warn("Unable to start PMP server: " + e);
				_pmpServer = null;
			}

			if (_pmpServer != null) {
				new Thread(_pmpServer).start();
			}
		}

		if (_settings.isAnyListenerEnabled()) {
			try {
				_anyServer = new ThriftServer(
									_settings.getAnyListenerPort(),
									new TAny2Any.Processor<TAny2AnyThriftProcessor>(new TAny2AnyThriftProcessor()));
			} catch (TTransportException e) {
				_logger.warn("Unable to start Any2Any server: " + e);
				_anyServer = null;
			}

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
