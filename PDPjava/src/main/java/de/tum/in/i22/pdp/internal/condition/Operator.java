package de.tum.in.i22.pdp.internal.condition;

import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.Mechanism;

public abstract class Operator
{
  public OperatorState state = new OperatorState();

  public Operator()
  {}
  
  public abstract void initOperatorForMechanism(Mechanism mech);
  public abstract boolean evaluate(Event curEvent);
  
}
