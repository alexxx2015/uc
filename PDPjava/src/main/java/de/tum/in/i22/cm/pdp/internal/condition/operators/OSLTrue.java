package de.tum.in.i22.cm.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.internal.Event;
import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.cm.pdp.xsd.TrueType;

public class OSLTrue extends TrueType
{
  private static Logger log = LoggerFactory.getLogger(OSLTrue.class);

  public OSLTrue()
  {}
  
  public OSLTrue(TrueType op, Mechanism curMechanism)
  {}
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
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
