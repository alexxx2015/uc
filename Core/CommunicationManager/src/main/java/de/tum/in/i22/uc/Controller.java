package de.tum.in.i22.uc;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import de.tum.in.i22.uc.cm.commandLineOptions.CommandLineOptions;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class Controller extends RequestHandler {
	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private IThriftServer _pdpServer;
	private IThriftServer _pipServer;
	private IThriftServer _pmpServer;
	private IThriftServer _anyServer;
	private IThriftServer _dmpServer;

	private String[] _args = null;

	public Controller() {
		this(new String[0]);
	}

	public Controller(String[] args) {
		_args = args;
		loadProperties(_args);
	}

	public static void main(String[] args) {
		Controller c = new Controller(args);
		if (c.start()) {
			// lock forever
			lock();
		} else {
			_logger.error("Unable to start UC infrastructure. Exiting.");
			System.exit(1);
		}
	}

	public boolean start() {
		if (!arePortsAvailable()) {
			return false;
		}

		super.init(Settings.getInstance().getPdpLocation(),
				Settings.getInstance().getPipLocation(), Settings.getInstance().getPmpLocation());

		_logger.info("Deploying initial policies ...");
		deployInitialPolicies();
		_logger.info("done.");

		if (Settings.getInstance().getStartServers()) {
			_logger.info("Starting up thrift servers");
			startListeners(this);
			do {
				try {
					_logger.info("... waiting ...");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					_logger.info(e.getMessage());
				}
			} while (!isStarted());
			_logger.info("Done. Thrift servers started.");
		}

		return true;
	}

	public boolean isStarted() {
		return (!Settings.getInstance().isPdpListenerEnabled() || _pdpServer != null && _pdpServer.started())
				&& (!Settings.getInstance().isPipListenerEnabled() || _pipServer != null && _pipServer.started())
				&& (!Settings.getInstance().isPmpListenerEnabled() || _pmpServer != null && _pmpServer.started())
				&& (!Settings.getInstance().isAnyListenerEnabled() || _anyServer != null && _anyServer.started())
				&& (!Settings.getInstance().isDistributionEnabled() || _dmpServer != null && _dmpServer.started());
	}

	/**
	 * Deploys the initial policies specified in the settings.
	 */
	private void deployInitialPolicies() {
		for (String uri : Settings.getInstance().getPmpInitialPolicies()) {
			IStatus status;

			/*
			 * Just calling deployPolicyURIPmp(uri) is _not_ OK, since this
			 * might be a remote method invocation and the URI would be resolved
			 * remotely. Instead, read the file locally and deploy as follows:
			 * -FK-
			 */
			try {
				status = deployPolicyRawXMLPmp(Files.toString(new File(uri), Charset.defaultCharset()));
			} catch (Exception e) {
				status = new StatusBasic(EStatus.ERROR, e.getMessage());
			}

			_logger.info(status.isStatus(EStatus.OKAY) ? "Deployed policy " + uri + " successfully."
					: "Error deploying policy " + uri + ": " + status.getErrorMessage() + ".");
		}
	}

	@Override
	public void stop() {
		if (_pdpServer != null)
			_pdpServer.stop();
		if (_pipServer != null)
			_pipServer.stop();
		if (_pmpServer != null)
			_pmpServer.stop();
		if (_anyServer != null)
			_anyServer.stop();
		if (_dmpServer != null)
			_dmpServer.stop();
		
		super.stop();
	}

	private void startListeners(IRequestHandler requestHandler) {
		if (Settings.getInstance().isPdpListenerEnabled()) {
			_pdpServer = ThriftServerFactory.createPdpThriftServer(Settings.getInstance().getPdpListenerPort(),
					requestHandler);

			if (_pdpServer != null) {
				new Thread(_pdpServer).start();
			}
		}

		if (Settings.getInstance().isPipListenerEnabled()) {
			_pipServer = ThriftServerFactory.createPipThriftServer(Settings.getInstance().getPipListenerPort(),
					requestHandler);

			if (_pipServer != null) {
				new Thread(_pipServer).start();
			}
		}

		if (Settings.getInstance().isPmpListenerEnabled()) {
			_pmpServer = ThriftServerFactory.createPmpThriftServer(Settings.getInstance().getPmpListenerPort(),
					requestHandler);

			if (_pmpServer != null) {
				new Thread(_pmpServer).start();
			}
		}

		if (Settings.getInstance().isAnyListenerEnabled()) {
			_anyServer = ThriftServerFactory.createAnyThriftServer(Settings.getInstance().getAnyListenerPort(),
					Settings.getInstance().getPdpListenerPort(), Settings.getInstance().getPipListenerPort(), Settings
							.getInstance().getPmpListenerPort());

			if (_anyServer != null) {
				new Thread(_anyServer).start();
			}
		}

		if (Settings.getInstance().isDistributionEnabled()) {
			_dmpServer = ThriftServerFactory.createDmpThriftServer(
					Settings.getInstance().getDmpListenerPort(), requestHandler);

			if (_dmpServer != null) {
				new Thread(_dmpServer).start();
			}
		}
	}

	private boolean arePortsAvailable() {
		boolean isPdpPortAvailable = !Settings.getInstance().isPdpListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPdpListenerPort());
		boolean isPipPortAvailable = !Settings.getInstance().isPipListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPipListenerPort());
		boolean isPmpPortAvailable = !Settings.getInstance().isPmpListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPmpListenerPort());
		boolean isAnyPortAvailable = !Settings.getInstance().isAnyListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getAnyListenerPort());
		boolean isDmpPortAvailable = !Settings.getInstance().isDistributionEnabled()
				|| isPortAvailable(Settings.getInstance().getDmpListenerPort());

		if (!isPdpPortAvailable || !isPipPortAvailable || !isPmpPortAvailable
				|| !isAnyPortAvailable || ! isDmpPortAvailable) {
			_logger.error("One of the ports is not available.");
			_logger.error("\nAre you sure you are not running another instance on the same ports?");
			return false;
		}
		return true;
	}

	private boolean isPortAvailable(int port) {
		Socket s = null;
		try {
			s = new Socket("localhost", port);
			// If the code makes it this far without an exception it means
			// that the port is available
			_logger.debug("Port " + port + " is not available");
			return false;
		} catch (IOException e) {
			_logger.debug("Port " + port + " is available");
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw new RuntimeException("You should handle this error.", e);
				}
			}
		}
	}

	static void loadProperties(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_PROPFILE)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PROPFILE));
		}

		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT)) {
			Settings.getInstance().loadSetting(CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT)) {
			Settings.getInstance().loadSetting(CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT)) {
			Settings.getInstance().loadSetting(CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT)) {
			Settings.getInstance().loadSetting(CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT)));
		}
	}

	protected static void lock() {
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void resetOnlyRequestHandler() {
		synchronized (this) {
			super.reset();
		}
	}

	@Override
	public boolean reset() {
		synchronized (this) {
			stop();
			resetOnlyRequestHandler();
			start();
		}
		return true;
	}
}
