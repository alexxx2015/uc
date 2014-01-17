package de.fraunhofer.iese.pef.pdp.internal.condition;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;

public abstract class Operator
{
  public OperatorState state = new OperatorState();

  public Operator()
  {}
  
  public abstract void initOperatorForMechanism(Mechanism mech);
  public abstract boolean evaluate(Event curEvent);
  
}
