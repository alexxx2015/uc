package pdp.tests;

import org.junit.Test;

import de.fraunhofer.iese.pef.pdp.LinuxPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;

public class PDPnativeTest
{
  private static IPolicyDecisionPoint lpdp;

  @Test
  public void test()
  {
    System.out.println("PDPnativeTest");
    
    // System.load only takes ABSOLUTE PATH!
    System.loadLibrary("pdp");
    lpdp=LinuxPolicyDecisionPoint.getInstance();
    
    int ret=lpdp.pdpStart();
    assert(ret==0);
    
    //System.out.println(System.getenv("PWD")); --> OldCPdp
    ret=lpdp.pdpDeployPolicy("src/main/xml/examples/testTUM.xml");
    assert(ret==0);
    
    Event e = new Event("testEvent", true);
    e.addStringParameter("val1", "value1");
    e.addStringParameter("val2", "value2");
    Decision d = lpdp.pdpNotifyEventJNI(e);
    System.out.println("Decision d: " + d);
    assert(d!=null);
    
    ret=lpdp.pdpStop();
    assert(ret==0);
  }

}
