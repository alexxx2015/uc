package de.tum.in.i22.uc.utilities;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Represents the initialization for the server logging with Log4J.
 */
public class PtpLogger {

	public static final String UNKNOWN_LEVEL = "UnknownLevel";
	private static Logger logger = Logger.getRootLogger();
	private String logdir;
	
	/**
	 * Initializes the logging for the echo server. Logs are appended to the 
	 * console output and written into a separated server log file at a given 
	 * destination.
	 * 
	 * @param logdir the destination (i.e. directory + filename) for the 
	 * 		persistent logging information.
	 * @param logdir 
	 * @throws IOException if the log destination could not be found.
	 */
	private PtpLogger(String type, String logdir, Level level) throws IOException {
		this.logdir = logdir;
		if(type.equals("TE"))
			initializeTranslationLogger(level);
		else if(type.equals("AE"))
			initializeAdapatationLogger(level);
	}

	private static PtpLogger translationLogging = null;
	public static PtpLogger translationLoggerInstance(){
		if(translationLogging == null){
			try {
				translationLogging = new PtpLogger("TE", "logs/translator.log",	Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return translationLogging;
	}
	
	private static PtpLogger adaptationLogging = null;
	public static PtpLogger adaptationLoggerInstance(){
		if(adaptationLogging == null){
			try {
				adaptationLogging = new PtpLogger("AE", "logs/adaptation.log", Level.ALL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return adaptationLogging;
	}
	
	private void initializeTranslationLogger(Level level) throws IOException {
		PatternLayout layout = new PatternLayout( "%d{ISO8601} %-5p [%t] %c: %m%n" );
		FileAppender fileAppender = new FileAppender( layout, logdir, true );		
	    
	   // ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		//logger.addAppender(consoleAppender);
		logger.addAppender(fileAppender);
		logger.setLevel(level);
	}
	
	private void initializeAdapatationLogger(Level level) throws IOException {
		PatternLayout layout = new PatternLayout( "%d{ISO8601} %-5p [%t] %c: %m%n" );
		FileAppender fileAppender = new FileAppender( layout, logdir, true );		
	    
	    ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		logger.addAppender(consoleAppender);
		logger.addAppender(fileAppender);
		logger.setLevel(level);
	}
	
	public static boolean isValidLevel(String levelString) {
		boolean valid = false;
		
		if(levelString.equals(Level.ALL.toString())) {
			valid = true;
		} else if(levelString.equals(Level.DEBUG.toString())) {
			valid = true;
		} else if(levelString.equals(Level.INFO.toString())) {
			valid = true;
		} else if(levelString.equals(Level.WARN.toString())) {
			valid = true;
		} else if(levelString.equals(Level.ERROR.toString())) {
			valid = true;
		} else if(levelString.equals(Level.FATAL.toString())) {
			valid = true;
		} else if(levelString.equals(Level.OFF.toString())) {
			valid = true;
		}
		
		return valid;
	}
	
	public boolean warnLog(String message, Exception exc){
		
		if(!isValidLevel("WARN"))
			return false;
		logger.warn(message, exc);
		return true;	
	}

	
	public  boolean infoLog(String message, Exception exc){
		if(!isValidLevel("INFO"))
			return false;
		logger.info(message, exc);
		return true;	
	}
	
	public  boolean debugLog(String message, Exception exc){
		if(!isValidLevel("DEBUG"))
			return false;
		logger.debug(message, exc);
		return true;	
	}
	
	public  boolean errorLog(String message, Exception exc){
		if(!isValidLevel("ERROR"))
			return false;
		logger.error(message, exc);
		return true;	
	}
	
	public  boolean fatalLog(String message, Exception exc){
		if(!isValidLevel("FATAL"))
			return false;
		logger.fatal(message, exc);
		return true;	
	}
	
	public Level getLogLevel(){
		return logger.getLevel();
	}
	public boolean setLogLevel(Level logLevel, String level) {
		if(!isValidLevel(level))
			return false;
		logger.setLevel(logLevel);
		return true;
		}
		
	
	public static String getPossibleLogLevels() {
		return "ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF";
	}

	
}
