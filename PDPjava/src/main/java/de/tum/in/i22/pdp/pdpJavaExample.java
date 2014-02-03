package de.tum.in.i22.pdp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.pdp.internal.Decision;
import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.IPolicyDecisionPoint;

public class pdpJavaExample
{
  private static Logger log = LoggerFactory.getLogger(pdpJavaExample.class);

  private static IPolicyDecisionPoint lpdp = null;
  
  public static void main(String[] args)
  {
    log.debug("pdpJavaExample");
    lpdp = PolicyDecisionPoint.getInstance();
    
    log.debug("lpdp: " + lpdp);
    boolean ret=lpdp.deployPolicy("src/main/resources/testTUM.xml");
    log.debug("Deploying policy returned: " + ret);
    
    log.debug("Deployed Mechanisms: [{}]", lpdp.listDeployedMechanisms());
    
    log.debug("Notifying event");
    Event e = new Event("testEvent", true, System.currentTimeMillis());
    e.addStringParameter("name1", "value1");
    
    Decision d = lpdp.notifyEvent(e);
    log.debug("d: {}", d);
    
    log.debug("revoking mechanism...");
    lpdp.revokePolicy("policyName");
    log.debug("terminating...");
  }

}
