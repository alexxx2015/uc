package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.xsd.NotType;

public class OSLNot extends NotType 
{
  private static Logger log = LoggerFactory.getLogger(OSLNot.class);
  
  public OSLNot()
  {}
  
  public OSLNot(Operator operand)
  {
    this.operators = operand;
  }

  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "! " + this.getOperators();
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    this.state.value = !((Operator)this.getOperators()).evaluate(curEvent);
    log.debug("eval NOT [{}]", this.state.value);
    return this.state.value;
  }
}
