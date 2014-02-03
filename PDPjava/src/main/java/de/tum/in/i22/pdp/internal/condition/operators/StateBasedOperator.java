package de.tum.in.i22.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType
{
  private static Logger log = LoggerFactory.getLogger(StateBasedOperator.class);
  
  public StateBasedOperator()
  {}
  
  public StateBasedOperator(StateBasedOperatorType op, Mechanism curMechanism)
  {
    log.debug("Processing StateBasedFormula from StateBasedOperatorType");
    this.operator = op.getOperator();
    this.param1   = op.getParam1();
    this.param2   = op.getParam2();
    this.param3   = op.getParam3();
  }
  
  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
  }
  
  @Override 
  public String toString()
  {
    return "StateBasedFormula [operator='"+this.getOperator()+"', param1='"+this.getParam1()+"', param2='"+this.getParam2()+"', param3='"+this.getParam3()+"']";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    log.debug("eval StateBasedFormula");
    // TODO: stateBasedFormula evaluation NYI: forward to PIP for evaluation 
    return true;
  }
}
