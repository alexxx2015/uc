package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.gproto.ConditionProto.PbTimeAmount;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.CircularArray;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.internal.condition.TimeAmount;
import de.fraunhofer.iese.pef.pdp.xsd.BeforeType;
import de.fraunhofer.iese.pef.pdp.xsd.time.TimeUnitType;

public class Before extends BeforeType 
{
  private static Logger         log        =LoggerFactory.getLogger(Before.class);
  public TimeAmount             timeAmount =null;
  
  // CircularArray cannot be declared in OperatorState, otherwise there are many "unchecked casts" required
  // because OperatorState can't declare the inner type of the circularArray 
  public CircularArray<Boolean> circArray = null;
  
  public Before()
  {}
  
  public Before(Operator operand, PbTimeAmount boundary)
  {
    this.setOperators(operand);
    this.setAmount(boundary.getAmount());
    this.setUnit(TimeUnitType.fromValue(boundary.getUnit()));
  }

  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
    this.circArray = new CircularArray<Boolean>(this.timeAmount.timestepInterval);
    for(int a=0; a<this.timeAmount.timestepInterval; a++)
      this.circArray.set(false, a);    
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "BEFORE ("+this.timeAmount + ", " + this.getOperators()+" )";
  }

  @Override
  public boolean evaluate(Event curEvent)
  { // before = at (currentTime - interval) op3 was true
    log.debug("circularArray: {}", this.circArray);
    
    Boolean curValue = this.circArray.readFirst();
    this.state.value = curValue;
    if(curEvent==null)
    {
      curValue=this.circArray.pop();
      Boolean operandState =  ((Operator)this.getOperators()).evaluate(curEvent);
      this.circArray.push(operandState);
      
      log.debug("circularArray: {}", this.circArray);
    }
    
    log.debug("eval BEFORE [{}]", this.state.value);
    return this.state.value;
  }
}
