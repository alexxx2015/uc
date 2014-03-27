package de.tum.in.i22.uc;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;

public class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	public static void main(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);

		// load properties
		if (cl.hasOption(CommandLineOptions.OPTION_PDP_PROPS)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PDP_PROPS));
		}

		new Thread(RequestHandler.getInstance()).start();

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

	public static boolean started() {
		return RequestHandler.getInstance().started();
	}
}
