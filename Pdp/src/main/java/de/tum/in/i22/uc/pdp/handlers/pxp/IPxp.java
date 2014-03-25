package de.tum.in.i22.uc.pdp.handlers.pxp;

import de.tum.in.i22.uc.pdp.core.Event;
import de.tum.in.i22.uc.pdp.core.ExecuteAction;


public interface IPxp
{
  public boolean execute(ExecuteAction execAction, Event event);
}