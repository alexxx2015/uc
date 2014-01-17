package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.xsd.AndType;

public class OSLAnd extends AndType
{
  private static Logger log = LoggerFactory.getLogger(OSLAnd.class);

  public OSLAnd()
  {}
  
  public OSLAnd(Operator operand1, Operator operand2)
  {
    this.getOperators().add(operand1);
    this.getOperators().add(operand2);
  }
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    ((Operator)this.getOperators().get(0)).initOperatorForMechanism(mech);
    ((Operator)this.getOperators().get(1)).initOperatorForMechanism(mech);
  }  

  public String toString()
  {
    return this.getOperators().get(0)+ " && " + this.getOperators().get(1);
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    this.state.value = ((Operator)this.getOperators().get(0)).evaluate(curEvent) && ((Operator)this.getOperators().get(1)).evaluate(curEvent);
    log.debug("eval AND [{}]", this.state.value );
    return this.state.value;
  }
}
