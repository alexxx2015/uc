package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.xsd.AlwaysType;

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
