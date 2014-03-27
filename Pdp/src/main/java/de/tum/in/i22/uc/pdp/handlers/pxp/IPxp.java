package de.tum.in.i22.uc.pdp.handlers.pxp;

import de.tum.in.i22.uc.cm.pdp.core.Event;
import de.tum.in.i22.uc.cm.pdp.core.IPdpExecuteAction;


public interface IPxp
{
  public boolean execute(IPdpExecuteAction execAction, Event event);
}