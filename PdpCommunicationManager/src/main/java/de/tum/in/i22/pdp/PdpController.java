package de.tum.in.i22.pdp;

import java.io.File;
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
import de.tum.in.i22.pdp.cm.in.pep.PepClientPipeHandler;
import de.tum.in.i22.pdp.cm.in.pep.PepFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pep.thrift.ThriftServer;
import de.tum.in.i22.pdp.cm.in.pip.PipFastServiceHandler;
import de.tum.in.i22.pdp.cm.in.pmp.PmpFastServiceHandler;
import de.tum.in.i22.pdp.core.IIncoming;
import de.tum.in.i22.pdp.injection.PdpModuleMockTestPip;
import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class PdpController {

	private static Logger _logger = Logger.getLogger(PdpController.class);

	private boolean _isStarted = false;

	private final IIncoming _pdpHandler;

	private final static Options _commandLineOptions = createCommandLineOptions();

	private final static String OPTION_HELP = "h";
	private final static String OPTION_PDP_PROPS = "pp";

	private Thread threadRequestHandler;
	private Thread threadPmpFastServiceHandler;
	private Thread threadPepGPBFastServiceHandler;
	private Thread threadPipFastServiceHandler;
	private Thread threadPepPipeHandler;

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
		if (_isStarted)
			return;
		_isStarted = true;

		_logger.info("Start pdp");

		_logger.info("Start Request Handler");
		RequestHandler eventHandler = RequestHandler.getInstance();
		// PDP handler is injected when PdpController is created
		eventHandler.setPdpHandler(_pdpHandler);
		this.threadRequestHandler = new Thread(eventHandler);
		this.threadRequestHandler.start();

		int pmpListenerPort = getPdpSettings().getPmpListenerPortNum();
		_logger.info("Start PmpFastServiceHandler on port: " + pmpListenerPort);
		this.threadPmpFastServiceHandler = new Thread(new PmpFastServiceHandler(pmpListenerPort));
		this.threadPmpFastServiceHandler.start();

		int pipListenerPort = getPdpSettings().getPipListenerPortNum();
		_logger.info("Start PipFastServiceHandler on port: " + pipListenerPort);
		this.threadPipFastServiceHandler = new Thread(new PipFastServiceHandler(pipListenerPort));
		this.threadPipFastServiceHandler.start();

		int pepGPBListenerPort = getPdpSettings().getPepGPBListenerPortNum();
		_logger.info("Start PepGPBFastServiceHandler on port: " + pepGPBListenerPort);
		this.threadPepGPBFastServiceHandler = new Thread(new PepFastServiceHandler(pepGPBListenerPort));
		this.threadPepGPBFastServiceHandler.start();

		File pepPipeIn = new File(getPdpSettings().getPepPipeIn());
		File pepPipeOut = new File(getPdpSettings().getPepPipeOut());
		if (pepPipeIn.exists() && !pepPipeIn.isDirectory() && pepPipeOut.exists() && !pepPipeOut.isDirectory()) {
			_logger.info("Start PepPipeHandler using pipes " + pepPipeIn + " and " + pepPipeOut);
			this.threadPepPipeHandler = new Thread(new PepClientPipeHandler(pepPipeIn, pepPipeOut));
			this.threadPepPipeHandler.start();
		}
		else {
			_logger.info("Did not start PepPipeHandler. Pipes " + pepPipeIn + " and " + pepPipeOut + " did not exist.");
		}

		int pepThriftListenerPort = getPdpSettings().getPepThriftListenerPortNum();
		_logger.info("Start PepThriftFastServiceHandler on port: " + pepThriftListenerPort);
		ThriftServer.createListener(pepThriftListenerPort, pepGPBListenerPort);
		ThriftServer.start();


	}

	public void stop(){
		// TODO These methods are deprecated for a good reason... Get rid of them.
		this.threadPepGPBFastServiceHandler.stop();
		this.threadPmpFastServiceHandler.stop();
		this.threadPipFastServiceHandler.stop();
		this.threadRequestHandler.stop();
		threadPepPipeHandler.stop();
		ThriftServer.stop();
	}

	public PdpSettings getPdpSettings() {
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

		/*
		 * Now that we've got the injector, we can build objects.
		 */
		PdpController pdp = injector.getInstance(PdpController.class);
		pdp.start();

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

	public IIncoming getPdpHandler(){
		return this._pdpHandler;
	}
}
