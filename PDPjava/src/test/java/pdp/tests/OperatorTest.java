package pdp.tests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;

public class OperatorTest
{
  private static Logger log = LoggerFactory.getLogger(OperatorTest.class);
  private static IPolicyDecisionPoint lpdp = null;
  
  public OperatorTest()
  {
    log.info("OperatorTest");
    lpdp = PolicyDecisionPoint.getInstance();
    log.debug("lpdp: " + lpdp);
  }

  @Test
  public void testBefore()
  {
    log.info("testBefore");
    boolean ret=lpdp.deployPolicy("src/test/resources/testBefore.xml");
    log.debug("Deploying test-policy returned: {}", ret);
    assert(ret==true);
    
    log.debug("Starting before-test");
    Event levent = new Event("action1", true);
    levent.addStringParameter("val1", "value1");
    
    for(int a=0; a<3; a++)
    {
      log.info("Notifying event");
      Decision d = lpdp.notifyEvent(levent);
      log.debug("Decision: {}", d);
      assert(a<2 == d.getAuthorizationAction().getType());
      
      try
      {
        log.debug("sleeping...");
        Thread.sleep(3000);
      }
      catch(InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }

}
