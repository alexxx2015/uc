package de.tum.in.i22.uc.pmp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.core.condition.CircularArray;
import de.tum.in.i22.uc.pmp.core.condition.Operator;
import de.tum.in.i22.uc.pmp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;
import de.tum.in.i22.uc.pmp.xsd.RepLimType;

public class RepLim extends RepLimType 
{
  private static Logger log        =LoggerFactory.getLogger(RepLim.class);
  public TimeAmount     timeAmount =null;
  
  public RepLim()
  {}

  @Override
  public void initOperatorForMechanism(IPmpMechanism mech)
  {
    this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
    this.state.circArray = new CircularArray<Boolean>(this.timeAmount.timestepInterval);
    for(int a=0; a<this.timeAmount.timestepInterval; a++)
      this.state.circArray.set(false, a);        
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "REPLIM ("+this.timeAmount + ", " + this.getOperators()+" )";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("circularArray: {}", this.state.circArray);
    if(this.state.counter >= this.getLowerLimit() && this.state.counter <= this.getUpperLimit())
      this.state.value = true;
    else this.state.value = false;
    
    if(curEvent==null)
    {
      Boolean curValue = this.state.circArray.pop();
      if(curValue)
      {
        this.state.counter--;
        log.debug("[REPLIM] Decrementing counter to [{}]", this.state.counter);
      }
      
      Boolean operandState = ((Operator)this.getOperators()).evaluate(curEvent);
      if(operandState)
      {
        this.state.counter++;
        log.debug("[REPLIM] Incrementing counter to [{}] due to intercepted event", this.state.counter);
      }
      
      this.state.circArray.push(operandState);
      log.debug("circularArray: {}", this.state.circArray);
    }
    
    log.debug("eval REPLIM [{}]", this.state.value);
    return this.state.value;
  }
}
