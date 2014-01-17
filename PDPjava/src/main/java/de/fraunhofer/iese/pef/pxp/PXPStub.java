package de.fraunhofer.iese.pef.pxp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.ExecuteAction;

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
