package de.tum.in.i22.cm.pxp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.cm.pdp.internal.Event;
import de.tum.in.i22.cm.pdp.internal.ExecuteAction;

public class PXPStub implements IPolicyExecutionPoint
{
  private static Logger log = LoggerFactory.getLogger(PXPStub.class);
  
  @Override
  public boolean execute(ExecuteAction execAction, Event event)
  {
    log.info("[PXPStub] Executing {} with parameters: {}", execAction.getName(), execAction.getParams());
    return true;
  }

}
