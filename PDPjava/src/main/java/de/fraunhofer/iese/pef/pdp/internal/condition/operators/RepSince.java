package de.fraunhofer.iese.pef.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.condition.Operator;
import de.fraunhofer.iese.pef.pdp.xsd.RepSinceType;

public class RepSince extends RepSinceType
{
  private static Logger log   =LoggerFactory.getLogger(RepSince.class);
  public long           limit =0;
  
  public RepSince()
  {}
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    ((Operator)this.getOperators().get(0)).initOperatorForMechanism(mech);
    ((Operator)this.getOperators().get(1)).initOperatorForMechanism(mech);
  }  

  public String toString()
  {
    String str="REPSINCE (" + this.getLimit() + ", " + this.getOperators().get(0)+ ", " + this.getOperators().get(1)+ " )";
    return str;
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    //TODO: RepSince evaluation NYI
    this.state.value = ((Operator)this.getOperators().get(0)).evaluate(curEvent) || ((Operator)this.getOperators().get(1)).evaluate(curEvent);
    log.debug("eval REPSINCE [{}]", this.state.value );
    return this.state.value;
  }
}
