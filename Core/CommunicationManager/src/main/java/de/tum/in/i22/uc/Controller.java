package de.tum.in.i22.uc;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class Controller {
	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private static Settings _settings;

	private static IThriftServer _pdpServer;
	private static IThriftServer _pipServer;
	private static IThriftServer _pmpServer;
	private static IThriftServer _anyServer;

	private static boolean isPortAvailable(int port) {
	    _logger.debug("--------------Testing port " + port);
	    Socket s = null;
	    try {
	        s = new Socket("localhost", port);
	        // If the code makes it this far without an exception it means
	        // something is using the port and has responded.
	        _logger.debug("--------------Port " + port + " is not available");
	        return false;
	    } catch (IOException e) {
	        _logger.debug("--------------Port " + port + " is available");
	        return true;
	    } finally {
	        if( s != null){
	            try {
	                s.close();
	            } catch (IOException e) {
	                throw new RuntimeException("You should handle this error." , e);
	            }
	        }
	    }
	}

	static void loadProperties(String[] args){
		CommandLine cl = CommandLineOptions.init(args);
		if (cl!=null && cl.hasOption(CommandLineOptions.OPTION_PROPFILE)) {
			Settings.setPropertiesFile(cl.getOptionValue(CommandLineOptions.OPTION_PROPFILE));
		}
		_settings = Settings.getInstance();
	}

	private static void lock() {
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		if (start(args)){
		/*
		 * Lock forever
		 */
		lock();
		}
		else{
			_logger.error("Exiting with status code 1");
			System.exit(1);
		}
	}

	public static boolean start(){
		return start(null);
	}

	public static boolean start(String[] args){
		/*
		 *  Load properties (if provided via parameter)
		 */
		loadProperties(args);

		/*
		 *  If ports are available...
		 */
		if (testIfPortsAreAvailable()){

			/*
			 *  ..start UC infrastructure
			 */
			startUC();
			return true;
		}
		/*
		 * ..otherwise return false
		 */
		return false;
	}



	public static boolean started() {
		return (!_settings.isPdpListenerEnabled() || (_pdpServer != null && _pdpServer.started()))
				&& (!_settings.isPipListenerEnabled() || (_pipServer != null && _pipServer.started()))
				&& (!_settings.isPmpListenerEnabled() || (_pmpServer != null && _pmpServer.started()))
				&& (!_settings.isAnyListenerEnabled() || (_anyServer != null && _anyServer.started()));
	}

	private static void startUC(){
		/*
		 * Start the queue handler
		 */
		IRequestHandler requestHandler = RequestHandler.getInstance();

		/*
		 * Start the request handlers
		 */
		startListeners(requestHandler);
	}


	private static void startListeners(IRequestHandler requestHandler) {
		if (_settings.isPdpListenerEnabled()) {
			_pdpServer = ThriftServerFactory.createPdpThriftServer(_settings.getPdpListenerPort(), requestHandler);

			if (_pdpServer != null) {
				new Thread(_pdpServer).start();
			}
		}


		if (_settings.isPipListenerEnabled()) {
			_pipServer = ThriftServerFactory.createPipThriftServer(_settings.getPipListenerPort(), requestHandler);

			if (_pipServer != null) {
				new Thread(_pipServer).start();
			}
		}

		if (_settings.isPmpListenerEnabled()) {
			_pmpServer = ThriftServerFactory.createPmpThriftServer(_settings.getPmpListenerPort(), requestHandler);

			if (_pmpServer != null) {
				new Thread(_pmpServer).start();
			}
		}

		if (_settings.isAnyListenerEnabled()) {
			_anyServer = ThriftServerFactory.createAnyThriftServer(_settings.getAnyListenerPort(),
												_settings.getPdpListenerPort(), _settings.getPipListenerPort(),
												_settings.getPmpListenerPort());

			if (_anyServer != null) {
				new Thread(_anyServer).start();
			}
		}
	}


	private static boolean testIfPortsAreAvailable() {
		boolean isPdpPortAvailable=isPortAvailable(_settings.getPdpListenerPort());
		boolean isPipPortAvailable=isPortAvailable(_settings.getPipListenerPort());
		boolean isPmpPortAvailable=isPortAvailable(_settings.getPmpListenerPort());
		boolean isAnyPortAvailable=isPortAvailable(_settings.getAnyListenerPort());

		if (!isPdpPortAvailable || !isPipPortAvailable || !isPmpPortAvailable|| !isAnyPortAvailable){
			_logger.error("One of the ports is not available.");
			_logger.error("pdpPort:	"+(isPdpPortAvailable?"":"NOT ")+"AVAILABLE");
			_logger.error("pipPort:	"+(isPipPortAvailable?"":"NOT ")+"AVAILABLE");
			_logger.error("pmpPort:	"+(isPmpPortAvailable?"":"NOT ")+"AVAILABLE");
			_logger.error("anyPort:	"+(isAnyPortAvailable?"":"NOT ")+"AVAILABLE");
			_logger.error("\nAre you sure you are not running another instance on the same ports?");
			return false;
		}
		return true;
	}

}
