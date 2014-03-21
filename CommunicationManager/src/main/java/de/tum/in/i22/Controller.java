package de.tum.in.i22;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.in.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;

public class Controller {

	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private static boolean _started = false;

	private Controller() {
	}


	public void start() {
		if (_started) {
			return;
		}
		_started = true;

		new Thread(RequestHandler.INSTANCE).start();;
	}

	public static void main(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);

		// load properties
		try {
			if (cl.hasOption(CommandLineOptions.OPTION_PDP_PROPS)) {
				Settings.getInstance(cl.getOptionValue(CommandLineOptions.OPTION_PDP_PROPS)).loadProperties();
			}
			else {
				Settings.getInstance().loadProperties();
			}
		} catch (IOException e) {
			_logger.error("Properties cannot be loaded.	", e);
			return;
		}

		new Controller().start();


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

	public static boolean isStarted() {
		return _started;
	}
}
