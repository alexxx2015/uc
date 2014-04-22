package de.tum.in.i22.uc.pdp.core.condition;

import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPdpMechanism;

public abstract class Operator
{
  public OperatorState state = new OperatorState();

  public Operator()
  {}

//  public abstract void initOperatorForMechanism(IPdpMechanism mech);
//  public abstract boolean evaluate(Event curEvent);
  public void initOperatorForMechanism(IPdpMechanism mech)
  {}
  
  public boolean evaluate(Event curEvent)
  {
    return false;
  }
  
}
