package de.tum.in.i22.pdp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.cm.in.RequestHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepClientNativeHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepClientPipeConnectionHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pep.thrift.ThriftServer;
import de.tum.in.i22.pdp.cm.in.pip.PipFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpFastServiceHandler;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.injection.PdpModuleMockTestPip;

public class PdpController {

	private static Logger _logger = Logger.getLogger(PdpController.class);

	private final IIncoming _pdpHandler;

	private final static Options _commandLineOptions = createCommandLineOptions();

	private final static String OPTION_HELP = "h";
	private final static String OPTION_PDP_PROPS = "pp";

	private static boolean _wasStarted = false;

	private static Thread _threadRequestHandler;
	private static Thread _threadPmpFastServiceHandler;
	private static Thread _threadPepGPBFastServiceHandler;
	private static Thread _threadPipFastServiceHandler;
	private static Thread _threadPepPipeHandler;
	private static Thread _threadThriftServer;

	private static boolean _startedRequestHandler = false;
	private static boolean _startedPmpFastServiceHandler = false;
	private static boolean _startedPepGPBFastServiceHandler = false;
	private static boolean _startedPipFastServiceHandler = false;
	private static boolean _startedPepPipeHandler = false;
	private static boolean _startedThriftServer = false;

	// FK: Although this class attribute is never used locally:
	// DO NOT REMOVE IT!!!
	// It will be used by native PEPs for dispatch events using JNI
	private static final PepClientNativeHandler nativePepHandler = new PepClientNativeHandler();

	/**
	 * Use dependency injection to inject pdpHandler.
	 *
	 * @param pdpHandler
	 */
	@Inject
	public PdpController(IIncoming pdpHandler) {
		_pdpHandler = pdpHandler;
	}


	public void start() {
		if (_wasStarted) {
			return;
		}
		_wasStarted = true;

		_logger.info("Start pdp");

		_logger.info("Start Request Handler");
		RequestHandler eventHandler = RequestHandler.getInstance();
		// PDP handler is injected when PdpController is created
		eventHandler.setPdpHandler(_pdpHandler);
		_threadRequestHandler = new Thread(eventHandler);
		_threadRequestHandler.start();
		_startedRequestHandler = true;

		startPepPipeListener();
		startPmpListener();
		startPipListener();
		startPepGpbListener();
		startPepThriftListener();
	}

	private void startPepThriftListener() {
		if (getPdpSettings().isPepThriftListenerEnabled()) {
			int pepThriftListenerPort = getPdpSettings().getPepThriftListenerPortNum();
			_logger.info("Start ThriftServer on port: " + pepThriftListenerPort);
			_threadThriftServer = new Thread (new ThriftServer(pepThriftListenerPort, getPdpSettings().getPepGPBListenerPortNum()));
			_threadThriftServer.start();
			_startedThriftServer = true;
		}
	}


	private void startPepPipeListener() {
		if (getPdpSettings().isPepPipeListenerEnabled()) {
			File pepPipeIn = new File(getPdpSettings().getPepPipeIn());
			File pepPipeOut = new File(getPdpSettings().getPepPipeOut());
			if (pepPipeIn.exists() && !pepPipeIn.isDirectory() && pepPipeOut.exists() && !pepPipeOut.isDirectory()) {
				_logger.info("Start PepPipeHandler using pipes " + pepPipeIn + " and " + pepPipeOut);
				try {
					_threadPepPipeHandler = new Thread(new PepClientPipeConnectionHandler(pepPipeIn, pepPipeOut));
				} catch (FileNotFoundException e) {
					_logger.warn("Unable to start pipe listener", e);
					return;
				}
				_threadPepPipeHandler.start();
				_startedPepPipeHandler = true;
			}
			else {
				_logger.info("Did not start PepPipeHandler. Pipes " + pepPipeIn + " and " + pepPipeOut + " did not exist.");
			}
		}
	}

	private void startPepGpbListener() {
		if (getPdpSettings().isPepGPBListenerEnabled()) {
			int pepGPBListenerPort = getPdpSettings().getPepGPBListenerPortNum();
			_logger.info("Start PepGPBFastServiceHandler on port: " + pepGPBListenerPort);
			_threadPepGPBFastServiceHandler = new Thread(new PepFastServiceHandler(pepGPBListenerPort));
			_threadPepGPBFastServiceHandler.start();
			_startedPepGPBFastServiceHandler = true;
		}
	}


	private void startPipListener() {
		if (getPdpSettings().isPipListenerEnabled()) {
			int pipListenerPort = getPdpSettings().getPipListenerPortNum();
			_logger.info("Start PipFastServiceHandler on port: " + pipListenerPort);
			_threadPipFastServiceHandler = new Thread(new PipFastServiceHandler(pipListenerPort));
			_threadPipFastServiceHandler.start();
			_startedPipFastServiceHandler = true;
		}
	}


	private void startPmpListener() {
		if (getPdpSettings().isPmpListenerEnabled()) {
			int pmpListenerPort = getPdpSettings().getPmpListenerPortNum();
			_logger.info("Start PmpFastServiceHandler on port: " + pmpListenerPort);
			_threadPmpFastServiceHandler = new Thread(new PmpFastServiceHandler(pmpListenerPort));
			_threadPmpFastServiceHandler.start();
			_startedPmpFastServiceHandler = true;
		}
	}


	public void stop(){
		// TODO These methods are deprecated for a good reason... Get rid of them.
		this._threadPepGPBFastServiceHandler.stop();
		this._threadPmpFastServiceHandler.stop();
		this._threadPipFastServiceHandler.stop();
		this._threadRequestHandler.stop();
		this._threadPepPipeHandler.stop();
		this._threadThriftServer.stop();
	}

	public static PdpSettings getPdpSettings() {
		return PdpSettings.getInstance();
	}

	private static Options createCommandLineOptions() {
		// Build available command line options
		OptionBuilder.withArgName("file");
		OptionBuilder.withLongOpt("pdp-properties");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("the file containing the PDP's properties");
		Option pdpProps = OptionBuilder.create(OPTION_PDP_PROPS);

		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("shows this help");
		Option help = OptionBuilder.create(OPTION_HELP);

		// assemble all options
		Options options = new Options();
		options.addOption(pdpProps);

		options.addOption(help);

		return options;
	}

	private static CommandLine parseCommandLineOptions(String[] args) {
		CommandLine line = null;
		HelpFormatter formatter = new HelpFormatter();

		try {
	        // parse the command line arguments
	        line = new GnuParser().parse(_commandLineOptions, args );
	    }
	    catch( ParseException exp ) {
	        System.err.println("Parsing of command line options failed.  Reason: " + exp.getMessage());
	    }

		if (line == null || line.hasOption(OPTION_HELP)) {
			formatter.printHelp("PdpController", _commandLineOptions);
			System.exit(0);
		}

		return line;
	}

	public static void main(String[] args) {
		// parse command line arguments
		CommandLine cl = parseCommandLineOptions(args);

		// load PDP properties
		try {
			if (cl.hasOption(OPTION_PDP_PROPS)) {
				PdpSettings.getInstance(cl.getOptionValue(OPTION_PDP_PROPS)).loadProperties();
			}
			else {
				PdpSettings.getInstance().loadProperties();
			}
		} catch (IOException e) {
			_logger.fatal("Properties cannot be loaded.	", e);
			return;
		}

		/*
		 * Guice.createInjector() takes your Modules, and returns a new Injector
		 * instance. Most applications will call this method exactly once, in
		 * their main() method.
		 */
		Injector injector = Guice.createInjector(new PdpModuleMockTestPip());
//		Injector injector = Guice.createInjector(new PdpModule());

		// build a PdpController object
		injector.getInstance(PdpController.class).start();

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
		return _wasStarted
				&& _startedRequestHandler
				&& (_startedPepGPBFastServiceHandler || !getPdpSettings().isPepGPBListenerEnabled())
				&& (_startedPepPipeHandler || !getPdpSettings().isPepPipeListenerEnabled())
				&& (_startedPipFastServiceHandler || !getPdpSettings().isPipListenerEnabled())
				&& (_startedPmpFastServiceHandler || !getPdpSettings().isPmpListenerEnabled())
				&& (_startedThriftServer || !getPdpSettings().isPepThriftListenerEnabled());
	}

	IIncoming getPdpHandler(){
		return this._pdpHandler;
	}
}
