package de.fraunhofer.iese.pef.pdp;



public class Win64PolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;

	public Win64PolicyDecisionPoint()  { 
	}
	

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
			loadDinamicLibrary("pdpNative/win64", "libiconv-2.dll");
			loadDinamicLibrary("pdpNative/win64", "libintl-8.dll");
			loadDinamicLibrary("pdpNative/win64", "libglib-2.0-0.dll");
			loadDinamicLibrary("pdpNative/win64", "pdp.dll");
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
		} catch (Exception e) {
			_logger.error("Could not load native PDP library! " + e.getMessage());
			throw e;
		}
	}

}
