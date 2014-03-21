package de.tum.in.i22.uc.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.internal.Event;
import de.tum.in.i22.uc.pdp.internal.Mechanism;
import de.tum.in.i22.uc.pdp.internal.condition.Operator;
import de.tum.in.i22.uc.pdp.internal.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.internal.gproto.ConditionProto.PbTimeAmount;
import de.tum.in.i22.uc.pdp.xsd.WithinType;
import de.tum.in.i22.uc.pdp.xsd.time.TimeUnitType;

public class Within extends WithinType 
{
  private static Logger log        =LoggerFactory.getLogger(Within.class);
  public TimeAmount     timeAmount =null;
  
  public Within()
  {}
  
  public Within(Operator operand, PbTimeAmount boundary)
  {
    this.operators = operand;
    this.setAmount(boundary.getAmount());
    this.setUnit(TimeUnitType.fromValue(boundary.getUnit()));
  }  

  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "WITHIN ("+this.timeAmount + ", " + this.getOperators()+" )";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("[WITHIN] Current state counter=[{}]", this.state.counter);
    if(this.state.counter>0) this.state.value=true;
    else this.state.value=false;
    
    if(curEvent==null)
    {
      boolean operandValue = ((Operator)this.getOperators()).evaluate(curEvent);
      if(operandValue)
      {
        this.state.counter=this.timeAmount.timestepInterval+1;
        log.debug("[WITHIN] Set negative counter to interval=[{}] due to subformulas state value=[{}]", this.state.counter, operandValue);
      }
      else
      {
        if(this.state.counter>0) this.state.counter--;
        log.debug("[WITHIN} New state counter: [{}]", this.state.counter);
      }

      // update state->value for logging output
      if(this.state.counter>0) this.state.value=true;
      else this.state.value=false;
    }

    log.debug("eval WITHIN [{}]", this.state.value);
    return this.state.value;
  }


}
