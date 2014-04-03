package de.tum.in.i22.uc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

abstract class CommandLineOptions {

	public final static Options options = createCommandLineOptions();

	final static String OPTION_HELP = "h";
	final static String OPTION_PROPFILE = "pp";


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

		// assemble all options
		Options options = new Options();
		options.addOption(pdpProps);

		options.addOption(help);

		return options;
	}

	static CommandLine init(String[] args) {
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
