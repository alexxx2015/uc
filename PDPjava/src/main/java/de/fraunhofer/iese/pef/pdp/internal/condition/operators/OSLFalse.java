package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.xsd.FalseType;

public class OSLFalse extends FalseType
{
  private static Logger log = LoggerFactory.getLogger(OSLFalse.class);

  public OSLFalse()
  {}
  
  public OSLFalse(FalseType op, Mechanism curMechanism)
  {}
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {}  
  
  @Override 
  public String toString()
  {
    return "FALSE";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("eval FALSE");
    return false;
  }
}
