package pdp.tests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.internal.IPolicyDecisionPoint;
import de.tum.in.i22.cm.pdp.internal.PolicyDecisionPoint;

public class PDPProtoBufTest
{
  private static Logger log = LoggerFactory.getLogger(PDPProtoBufTest.class);
  private static IPolicyDecisionPoint lpdp = null;

  @Test
  public void test()
  {
    log.info("PDP w/ gpb test");
    lpdp = PolicyDecisionPoint.getInstance();
    log.debug("lpdp: " + lpdp);

    boolean ret=lpdp.deployPolicy("src/test/resources/policyTest.gpb");
    log.debug("Deploying policy returned: " + ret);
    assert(ret==true);
    log.debug("Deployed Mechanisms: [{}]", lpdp.listDeployedMechanisms());
  }

}
