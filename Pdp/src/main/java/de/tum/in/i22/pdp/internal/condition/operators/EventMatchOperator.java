package de.tum.in.i22.pdp.internal.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.pdp.internal.ActionDescriptionStore;
import de.tum.in.i22.pdp.internal.Event;
import de.tum.in.i22.pdp.internal.EventMatch;
import de.tum.in.i22.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.internal.gproto.EventProto.PbEvent;

public class EventMatchOperator extends EventMatch
{
  private static Logger log = LoggerFactory.getLogger(EventMatchOperator.class);
  
  public EventMatchOperator()
  {}
  
//  public EventMatchOperator(EventMatch op, Mechanism curMechanism)
//  {
//    this((EventMatchingOperatorType)op, curMechanism);
//  }
//  
//  public EventMatchOperator(EventMatchingOperatorType op, Mechanism curMechanism)
//  {
//    super(op, curMechanism);
//    
//    ActionDescriptionStore ads = ActionDescriptionStore.getInstance();
//    ads.addEventMatch(this);
//  }
  
  public EventMatchOperator(PbEvent pbEvent)
  {
    super(pbEvent);
  }

  @Override
  public void initOperatorForMechanism(Mechanism mech)
  {
    ActionDescriptionStore ads = ActionDescriptionStore.getInstance();
    ads.addEventMatch(this);
  }  
  
  @Override
  public boolean evaluate(Event curEvent)
  {
    boolean ret = false;
    if(curEvent!=null)
    {
      if(eventMatches(curEvent)) this.state.value=true;
      ret=this.state.value;
    }
    else
    { // reset at end of timestep (i.e. curEvent==null)
      ret=this.state.value;
      this.state.value=false;
    }
    log.debug("eval EVENTMATCH [{}]", ret);
    return ret;
  }
}
