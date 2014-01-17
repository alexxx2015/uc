package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.xsd.EvalOperatorType;

public class EvalOperator extends EvalOperatorType
{
  private static Logger log     =LoggerFactory.getLogger(EvalOperator.class);
  
  public EvalOperator()
  {}
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {}  
  
  @Override 
  public String toString()
  {
    return "EvalOperator [Type: "+this.getType()+", ["+this.getContent()+"]]";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("eval EvalOperator");
    // TODO: evalOperator evaluation NYI
    return false;
  }
}
