package de.tum.in.i22.uc.pmp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.core.condition.Operator;
import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;
import de.tum.in.i22.uc.pmp.xsd.OrType;

public class OSLOr extends OrType
{
  private static Logger log = LoggerFactory.getLogger(OSLOr.class);

  public OSLOr()
  {}
  
  public OSLOr(Operator operand1, Operator operand2)
  {
    this.getOperators().add(operand1);
    this.getOperators().add(operand2);
  }
  
  @Override
  public void initOperatorForMechanism(IPmpMechanism mech)
  {
    ((Operator)this.getOperators().get(0)).initOperatorForMechanism(mech);
    ((Operator)this.getOperators().get(1)).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    String str="(" + this.getOperators().get(0)+ " || " + this.getOperators().get(1) + ")";
    return str;
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    Boolean op1state = ((Operator)this.getOperators().get(0)).evaluate(curEvent);
    Boolean op2state = ((Operator)this.getOperators().get(1)).evaluate(curEvent);
    this.state.value = op1state || op2state;
    log.debug("eval OR [{}]", this.state.value );
    return this.state.value;
  }
}
