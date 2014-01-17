package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
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
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "REPLIM ("+this.timeAmount + ", " + this.getOperators()+" )";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    // TODO: RepLim evaluation NYI
    //this.state.value = !this.operand1.evaluate(curEvent);
    log.debug("eval REPLIM [{}]", this.state.value);
    return this.state.value;
  }
}
