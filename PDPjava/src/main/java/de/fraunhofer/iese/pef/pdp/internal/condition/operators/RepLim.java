package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.CircularArray;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.internal.condition.TimeAmount;
import de.fraunhofer.iese.pef.pdp.xsd.RepLimType;

public class RepLim extends RepLimType 
{
  private static Logger log        =LoggerFactory.getLogger(RepLim.class);
  public TimeAmount     timeAmount =null;
  
  public RepLim()
  {}

  @Override
  public void initOperatorForMechanism(Mechanism mech)
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
