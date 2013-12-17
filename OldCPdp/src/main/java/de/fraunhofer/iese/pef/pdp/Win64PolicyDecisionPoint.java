package de.fraunhofer.iese.pef.pdp;

import java.io.File;

import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;

public class Win64PolicyDecisionPoint extends PolicyDecisionPoint
{
  private static final long serialVersionUID =1L;

  public Win64PolicyDecisionPoint()
  {
  }

  @Override
  public void initialize() throws Exception
  {
    _logger.info("Loading native PDP library");
    try
    {
      String os=System.getProperty("os.name").toLowerCase();
      System.out.println("os: " + os);
      if(os.startsWith("win")) os="win";
      
      String arch=System.getProperty("os.arch").toLowerCase(); 
      // --> will result i386 instead of i686 which is used by native part (python)...
      if(arch.equals("i386")) arch="i686";
      if(arch.equals("amd64")) arch="64";
      System.out.println("arch: " + arch);

      // loadDinamicLibrary("pdpNative/win64", "libiconv-2.dll");
      // loadDinamicLibrary("pdpNative/win64", "libintl-8.dll");
      // loadDinamicLibrary("pdpNative/win64", "libglib-2.0-0.dll");
      //loadDinamicLibrary("pdpNative/win64", "pdp.dll");
      // TODO: I will look for a more convenient way of adressing the native lib dependent of platform...
      File libFile = new File("../PdpCore/target/natives/nativeLibs/"+os+arch+"/pdp.dll");
      System.load(libFile.getCanonicalPath());
      
      _logger.info("starting returned: " + this.pdpStart());
      File polExampleFile = new File("../OldCPdp/src/main/xml/examples/testTUM.xml");
      int ret=this.pdpDeployPolicy(polExampleFile.getCanonicalPath());
      _logger.info("policy should be deployed... "+ret);
      
      pdpRunning=true;
      _logger.info("Native PDP library loaded...");
//		Event e = new Event("testEvent", true);
//		e.addStringParameter("val1", "value1");
//		e.addStringParameter("val2", "value2");
//		Decision d = this.pdpNotifyEventJNI(e);
//		_logger.info("decision: " + d);      
    }
    catch(Exception e)
    {
      _logger.error("Could not load native PDP library! " + e.getMessage());
      throw e;
    }
  }

}
