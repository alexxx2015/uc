package de.fraunhofer.iese.pef.pxp;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.ExecuteAction;


public interface IPolicyExecutionPoint
{
  public boolean execute(ExecuteAction execAction, Event event);
}