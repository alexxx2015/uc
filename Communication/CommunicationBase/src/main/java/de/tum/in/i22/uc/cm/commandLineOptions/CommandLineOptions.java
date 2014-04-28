package de.tum.in.i22.uc.cm.commandLineOptions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

abstract public class CommandLineOptions {

	public final static Options options = createCommandLineOptions();

	final public static String OPTION_HELP = "h";
	final public static String OPTION_PROPFILE = "pp";
	final public static String OPTION_LOCAL_PDP_LISTENER_PORT = "pdp";
	final public static String OPTION_LOCAL_PIP_LISTENER_PORT = "pip";
	final public static String OPTION_LOCAL_PMP_LISTENER_PORT = "pmp";
	final public static String OPTION_LOCAL_ANY_LISTENER_PORT = "any";

	final public static String OPTION_LOCAL_PDP_LISTENER_PORT_LONG = "pdpListenerPort";
	final public static String OPTION_LOCAL_PIP_LISTENER_PORT_LONG = "pipListenerPort";
	final public static String OPTION_LOCAL_PMP_LISTENER_PORT_LONG = "pmpListenerPort";
	final public static String OPTION_LOCAL_ANY_LISTENER_PORT_LONG = "anyListenerPort";

	private static Options createCommandLineOptions() {
		// Build available command line options
		OptionBuilder.withArgName("file");
		OptionBuilder.withLongOpt("pdp-properties");
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("the file containing the PDP's properties");
		Option pdpProps = OptionBuilder.create(OPTION_PROPFILE);

		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("shows this help");
		Option help = OptionBuilder.create(OPTION_HELP);

		OptionBuilder.hasArg(true);
		OptionBuilder.withLongOpt(OPTION_LOCAL_PDP_LISTENER_PORT_LONG);
		OptionBuilder.withDescription("port which the (possibly) created pdpServer is going to listen on");
		Option pdpPort = OptionBuilder.create(OPTION_LOCAL_PDP_LISTENER_PORT);

		OptionBuilder.hasArg(true);
		OptionBuilder.withLongOpt(OPTION_LOCAL_PIP_LISTENER_PORT_LONG);
		OptionBuilder.withDescription("port which the (possibly) created pipServer is going to listen on");
		Option pipPort = OptionBuilder.create(OPTION_LOCAL_PIP_LISTENER_PORT);

		OptionBuilder.hasArg(true);
		OptionBuilder.withLongOpt(OPTION_LOCAL_PMP_LISTENER_PORT_LONG);
		OptionBuilder.withDescription("port which the (possibly) created pmpServer is going to listen on");
		Option pmpPort = OptionBuilder.create(OPTION_LOCAL_PMP_LISTENER_PORT);

		OptionBuilder.hasArg(true);
		OptionBuilder.withLongOpt(OPTION_LOCAL_ANY_LISTENER_PORT_LONG);
		OptionBuilder.withDescription("port which the (possibly) created anyServer is going to listen on");
		Option anyPort = OptionBuilder.create(OPTION_LOCAL_ANY_LISTENER_PORT);

		// assemble all options
		Options options = new Options();
		options.addOption(pdpProps);
		options.addOption(help);
		options.addOption(pdpPort);
		options.addOption(pipPort);
		options.addOption(pmpPort);
		options.addOption(anyPort);
		
		return options;
	}

	public static CommandLine init(String[] args) {
		if (args==null) return null;
		CommandLine line = null;
		HelpFormatter formatter = new HelpFormatter();

		try {
	        // parse the command line arguments
	        line = new GnuParser().parse(options, args );
	    }
	    catch( ParseException exp ) {
	        System.err.println("Parsing of command line options failed.  Reason: " + exp.getMessage());
	    }

		if (line == null || line.hasOption(OPTION_HELP)) {
			formatter.printHelp("PdpController", options);
			System.exit(0);
		}

		return line;
	}
}
