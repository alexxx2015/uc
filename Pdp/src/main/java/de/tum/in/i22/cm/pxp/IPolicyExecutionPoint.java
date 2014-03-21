package de.tum.in.i22.cm.pxp;

import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.ExecuteAction;


public interface IPolicyExecutionPoint
{
  public boolean execute(ExecuteAction execAction, Event event);
}