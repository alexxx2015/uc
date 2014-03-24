package de.tum.in.i22.uc.pdp.handlers.pxp;

import de.tum.in.i22.uc.pdp.internal.Event;
import de.tum.in.i22.uc.pdp.internal.ExecuteAction;


public interface IPxp
{
  public boolean execute(ExecuteAction execAction, Event event);
}