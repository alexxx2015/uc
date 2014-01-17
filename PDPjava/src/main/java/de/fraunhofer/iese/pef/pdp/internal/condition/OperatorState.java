package de.fraunhofer.iese.pef.pdp.internal.condition;

public class OperatorState
{
  public boolean value = false;
  public boolean immutable = false;
  
  public long counter = 0;
  
  // still used? currently not all operator evaluations implemented, so wait...
  public boolean subEverTrue = false;
  
}
