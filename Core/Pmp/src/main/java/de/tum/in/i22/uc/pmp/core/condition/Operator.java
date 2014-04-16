package de.tum.in.i22.uc.pmp.core.condition;

import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;

public abstract class Operator
{
  public OperatorState state = new OperatorState();

  public Operator()
  {}

  public abstract void initOperatorForMechanism(IPmpMechanism mech);
  public abstract boolean evaluate(Event curEvent);
  
}
