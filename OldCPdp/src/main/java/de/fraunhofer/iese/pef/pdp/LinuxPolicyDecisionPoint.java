package de.fraunhofer.iese.pef.pdp;

import java.io.File;

import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;

public class LinuxPolicyDecisionPoint extends PolicyDecisionPoint {
	private static final long serialVersionUID = 1L;

	private static IPolicyDecisionPoint _instance = null;
	public LinuxPolicyDecisionPoint() { 
	}
	
	public static IPolicyDecisionPoint  getInstance()
	{
		if(_instance==null)	_instance=new LinuxPolicyDecisionPoint();
		return _instance;
	}

	@Override
	public void initialize() throws Exception {
		_logger.info("Loading native PDP library");
		try {
//			String relativePath= "/" + "../natives/nativeLibs" + "/" + "libpdp.so";
//			URL dll = PolicyDecisionPoint.class.getResource(relativePath);
//			_logger.info("Loading: " + dll.toURI());
			
		  String os = System.getProperty( "os.name" ).toLowerCase();
		  System.out.println("os: " + os);
		  String arch=System.getProperty( "os.arch" ).toLowerCase(); //--> will result i386 instead of i686 which is used by native part...
		  if(arch.equals("i386")) arch="i686";
		  System.out.println("arch: " + arch);
		  
		  File libFile = new File("../PdpCore/target/natives/nativeLibs/"+os+"-"+arch+"/libpdp.so");
			//loadDinamicLibrary("../natives/nativeLibs", "libpdp.so");
		  
			//System.load("/home/rd/projects/tum/pdpX1/pdp/OldCPdp/target/nativeLibs/libpdp.so");
		  System.load(libFile.getCanonicalPath());
			pdpRunning = true;
			_logger.info("Native PDP library loaded...");
			_logger.info("starting returned: " + this.pdpStart());
			//TODO FIXME: replace with relative path
			int ret=this.pdpDeployPolicy("/home/rd/projects/tum/pdpX1/pdp/OldCPdp/src/main/xml/examples/testTUM.xml");
			_logger.info("policy should be deployed... "+ret);
//			_logger.info("mechanisms: " + this.listDeployedMechanismsJNI());

			//Event e = new Event("testEvent", true);
			//e.addStringParameter("val1", "value1");
			//e.addStringParameter("val2", "value2");
			//Decision d = this.pdpNotifyEventJNI(e);
			//_logger.info("decision: " + d);
		} catch (Exception e) {
			_logger.error("Could not load native PDP library! " + e.getMessage());
			throw e;
		}
	}
	
}
