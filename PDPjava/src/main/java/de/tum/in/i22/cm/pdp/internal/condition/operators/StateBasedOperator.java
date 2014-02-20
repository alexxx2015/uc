package de.tum.in.i22.cm.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.PolicyDecisionPoint;
import de.tum.in.i22.cm.pdp.internal.Event;
import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.cm.pdp.xsd.StateBasedOperatorType;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;

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
    IPdpEngine2PipCacher engine2PipCacher = PolicyDecisionPoint.get_engine2PipCacher();
    if(engine2PipCacher != null){
	    if(curEvent == null)
	    	return engine2PipCacher.evaluatePredicateCurrentState(this.operator+"|"+this.param1+"|"+this.param2+"|"+this.param3);
	    
	    return engine2PipCacher.evaluatePredicateSimulatingNextState(curEvent.toIEvent(), this.operator+"|"+this.param1+"|"+this.param2+"|"+this.param3);
    }
    return false;
  }
}
