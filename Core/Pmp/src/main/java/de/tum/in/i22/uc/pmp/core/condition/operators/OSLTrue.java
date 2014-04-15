package de.tum.in.i22.uc.pmp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;
import de.tum.in.i22.uc.pmp.xsd.TrueType;

public class OSLTrue extends TrueType
{
  private static Logger log = LoggerFactory.getLogger(OSLTrue.class);

  public OSLTrue()
  {}
  
  public OSLTrue(TrueType op, IPmpMechanism curMechanism)
  {}
  
  @Override
  public void initOperatorForMechanism(IPmpMechanism mech)
  {}  
  
  @Override 
  public String toString()
  {
    return "TRUE";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("eval TRUE");
    return true;
  }
}
