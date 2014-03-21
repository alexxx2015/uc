package de.tum.in.i22.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.internal.condition.Operator;
import de.tum.in.i22.pdp.xsd.AlwaysType;

public class Always extends AlwaysType 
{
  private static Logger log = LoggerFactory.getLogger(Always.class);
  
  public Always()
  {}
  
  public Always(Operator operand1)
  {
    this.setOperators(operand1);
  }  

  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }
  
  public String toString()
  {
    return "ALWAYS ("+this.getOperators()+")";
  }
  

  @Override
  public boolean evaluate(Event curEvent)
  {
    if(!this.state.immutable)
    {
      this.state.value = ((Operator)this.getOperators()).evaluate(curEvent);
      if(!this.state.value && curEvent==null)
      {
        log.debug("evaluating ALWAYS: activating IMMUTABILITY");
        this.state.immutable = true;
      }
    }
    log.debug("eval ALWAYS [{}]", this.state.value);
    return this.state.value;
  }
}
