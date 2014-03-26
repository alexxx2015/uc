package pdp.tests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.pdp.core.Event;
import de.tum.in.i22.uc.cm.pdp.core.IPdpMechanism;
import de.tum.in.i22.uc.cm.pdp.core.IPolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class PDPJavaTest
{
  private static Logger log = LoggerFactory.getLogger(PDPJavaTest.class);

  private static IPolicyDecisionPoint lpdp = null;

  @Test
  public void test()
  {
    System.out.println("PDPJavaTest");
    lpdp = PolicyDecisionPoint.getInstance();

    System.out.println("lpdp: " + lpdp);
    boolean ret=lpdp.deployPolicy("src/test/resources/testTUM.xml");
    System.out.println("Deploying policy returned: " + ret);

    log.debug("Deployed Mechanisms: [{}]", lpdp.listDeployedMechanisms());

    log.debug("Notifying event");
    Event e = new Event("testEvent", true, System.currentTimeMillis());
    e.addStringParameter("name1", "value1");
    e.addStringParameter("name2", "value2");

    IPdpMechanism d = lpdp.notifyEvent(e);
    log.debug("d: [{}]", d);

    log.debug("revoking test mechanism...");
    ret=lpdp.revokePolicy("testPolicy");
    log.info("terminating...");

    assert(true);
  }

}
