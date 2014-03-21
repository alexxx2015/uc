package de.tum.in.i22.uc.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.internal.Event;
import de.tum.in.i22.uc.pdp.internal.Mechanism;
import de.tum.in.i22.uc.pdp.internal.condition.Operator;
import de.tum.in.i22.uc.pdp.xsd.AndType;

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
    Boolean op1state = ((Operator)this.getOperators().get(0)).evaluate(curEvent);
    Boolean op2state = ((Operator)this.getOperators().get(1)).evaluate(curEvent);
    this.state.value = op1state && op2state;
    log.debug("eval AND [{}]", this.state.value );
    return this.state.value;
  }
}
