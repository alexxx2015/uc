package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.pdp.core.condition.Condition;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.handlers.pxp.IPxp;
import de.tum.in.i22.uc.pdp.handlers.pxp.PXPStub;
import de.tum.in.i22.uc.pdp.xsd.AuthorizationActionType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;
import de.tum.in.i22.uc.pdp.xsd.PreventiveMechanismType;
import de.tum.in.i22.uc.pdp.xsd.action.ExecuteAsyncActionType;

public class Mechanism extends Thread implements IPdpMechanism
{
  private static Logger log = LoggerFactory.getLogger(Mechanism.class);

  private String              mechanismName       =null;
  private String              description         =null;
  private long                lastUpdate          =0;
  private long                timestepSize        =0;
  private long                timestep            =0;
  private EventMatch          triggerEvent        =null;
  private Condition           condition           =null;
  private AuthorizationAction authorizationAction =null;
  private List<ExecuteAction> executeAsyncActions =new ArrayList<ExecuteAction>();

  private IPolicyDecisionPoint pdp = null;

  public boolean isStarted = false;
  private Thread updateThread = null;

  public Mechanism()
  {}

  public Mechanism(MechanismBaseType mech) throws InvalidMechanismException
  {
    log.debug("Preparing mechanism from MechanismBaseType");
    this.mechanismName = mech.getName();
    this.description = mech.getDescription();
    this.lastUpdate = 0;
    this.timestepSize = mech.getTimestep().getAmount() * TimeAmount.getTimeUnitMultiplier(mech.getTimestep().getUnit());
    this.timestep = 0;
    if(mech instanceof PreventiveMechanismType)
    {
      PreventiveMechanismType curMech = (PreventiveMechanismType) mech;
      log.debug("Processing PreventiveMechanism");

      this.triggerEvent = new EventMatch(curMech.getTrigger(), this);

      ActionDescriptionStore ads = ActionDescriptionStore.getInstance();
      ads.addMechanism(this);

      // TODO: subscription to PEP?!

      log.debug("Preparing AuthorizationAction from List<AuthorizationActionType>: {} entries", curMech.getAuthorizationAction().size());
      HashMap<String, AuthorizationAction> authActions = new HashMap<String, AuthorizationAction>();

      for(AuthorizationActionType auth : curMech.getAuthorizationAction())
      {
        log.debug("Found authAction {}", auth.getName());
        if(auth.isSetStart() || curMech.getAuthorizationAction().size()==1)
          authActions.put("start", new AuthorizationAction(auth));
        else authActions.put(auth.getName(), new AuthorizationAction(auth));
      }

      log.debug("Preparing hierarchy of authorizationActions (list: {})", authActions.size());
      this.authorizationAction = authActions.get("start");

      if(curMech.getAuthorizationAction().size()>1)
      {
        AuthorizationAction curAuth = this.authorizationAction;
        log.debug("starting with curAuth: {}", curAuth.getName());
        do
        {
          log.debug("searching for fallback={}", curAuth.getFallbackName());
          if(!curAuth.getFallbackName().equalsIgnoreCase("allow") && !curAuth.getFallbackName().equalsIgnoreCase("inhibit"))
          {
            AuthorizationAction fallbackAuth =authActions.get(curAuth.getFallbackName());
            if(fallbackAuth==null)
            {
              log.error("Requested fallback authorizationAction {} not found!", curAuth.getFallbackName());
              throw new InvalidMechanismException("Requested fallback authorizationAction not specified");
            }
            curAuth.setFallback(fallbackAuth);
            log.debug("  set fallback to {}", curAuth.getFallback().getName());
          }
          else
          {
            if(curAuth.getFallbackName().equalsIgnoreCase("allow"))
            {
              curAuth.setFallback(AuthorizationAction.AUTHORIZATION_ALLOW);
            }
            log.debug("  set fallback to static {}", curAuth.getFallback().getName());
            break;
          }
          curAuth = curAuth.getFallback();
        }
        while(true);
      }
      log.debug("AuthorizationActions successfully processed.");
    }

    this.condition = new Condition(mech.getCondition(), this);

    log.debug("Processing executeAsyncActions");
    // Processing synchronous executeActions for allow
    for(ExecuteAsyncActionType execAction : mech.getExecuteAsyncAction())
    {
      this.executeAsyncActions.add(new ExecuteAction(execAction));
    }
  }

  public boolean init(IPolicyDecisionPoint pdp)
  {
    log.debug("Initializing mechanism update thread");
    this.pdp = pdp;
    this.lastUpdate = System.currentTimeMillis();
    this.updateThread = new Thread(this);
    this.updateThread.start();
    //Populate pip depending on the current condition
    this.condition.operator.initOperatorForMechanism(this);
    isStarted = true;
    return isStarted;
  }

  @Override
public String getMechanismName()
  {
    return mechanismName;
  }

  public void setMechanismName(String name)
  {
    this.mechanismName=name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description=description;
  }

  public AuthorizationAction getAuthorizationAction()
  {
    return authorizationAction;
  }

  public void setAuthorizationAction(AuthorizationAction authorizationAction)
  {
    this.authorizationAction=authorizationAction;
  }

  public List<ExecuteAction> getExecuteAsyncActions()
  {
    return executeAsyncActions;
  }

  public void setExecuteAsyncActions(List<ExecuteAction> executeAsyncActions)
  {
    this.executeAsyncActions=executeAsyncActions;
  }

  public EventMatch getTriggerEvent()
  {
    return triggerEvent;
  }

  public void setTriggerEvent(EventMatch triggerEvent)
  {
    this.triggerEvent=triggerEvent;
  }

  public long getTimestepSize()
  {
    return timestepSize;
  }

  public void setTimestepSize(long timestepSize)
  {
    this.timestepSize=timestepSize;
  }

  @Override
public boolean revoke()
  {
    this.updateThread.interrupt();
    return true;
  }

  public synchronized Decision notifyEvent(Event curEvent, Decision d)
  {
    log.debug("updating mechanism [{}]", this.getMechanismName());
    if(this.triggerEvent.eventMatches(curEvent))
    {
      log.info("Event matches -> evaluating condition");
      boolean ret = this.condition.evaluate(curEvent);
      if(ret)
      {
        log.info("Condition satisfied; merging mechanism into decision");
        d.processMechanism(this, curEvent);
      }
      else log.info("condition NOT satisfied");
    }

    return d;
  }

  private synchronized boolean mechanismUpdate()
  { // TODO improve accuracy to microseconds?
    long now = System.currentTimeMillis();
    long elapsedLastUpdate = now - this.lastUpdate;
    long difference = elapsedLastUpdate - (this.timestepSize/1000);

    if(difference<0)
    { // Aborting update because the timestep has not yet passed
      log.trace("[{}] Timestep remaining {} -> timestep has not yet passed", this.mechanismName, difference);
      log.trace("##############################################################################################################");
      return false;
    }

    // Correct time substracting possible delay in the execution because difference between timestep and last time
    // mechanism was updated will not be exactly the timestepSize
    this.lastUpdate = now-difference;
    if(difference > this.timestepSize)
    {
      log.warn("[{}] Timestep difference is larger than mechanism's timestep size => we missed to evaluate at least one timestep!!", this.mechanismName);
      log.warn("--------------------------------------------------------------------------------------------------------------");
    }

    timestep++;
    log.debug("////////////////////////////////////////////////////////////////////////////////////////////////////////////");
    log.debug("[{}] Null-Event updating {}. timestep at interval of {} us", this.mechanismName, this.timestep, this.timestepSize);

    boolean conditionValue=this.condition.evaluate(null);
    log.debug("conditionValue: {}", conditionValue);
    log.debug("////////////////////////////////////////////////////////////////////////////////////////////////////////////");

    return conditionValue;
  }


  @Override
  public void run()
  {
    long sleepValue = this.timestepSize / 1000;
    log.info("Started mechanism update thread usleep={} ms", sleepValue);

    while(!isInterrupted())
    {
      try
      {
        boolean mechanismValue = this.mechanismUpdate();
        if(mechanismValue)
        {
          log.info("Mechanism condition satisfied; triggered optional executeActions");
          for(ExecuteAction execAction : this.getExecuteAsyncActions())
          {
            if(execAction.getProcessor().equals("pep"))
              log.warn("Timetriggered execution of executeAction [{}] not possible with processor PEP", execAction.getName());
            else
            {
              log.debug("Execute asynchronous action [{}]", execAction.getName());
              // TODO: Execution should be forwarded to appropriate execution instance!
              IPxp pxp = new PXPStub();
              pxp.execute(execAction, null);
            }
          }
        }

        if(interrupted())
        {
          log.info("Mechanism thread was interrupted. terminating...");
          return;
        }

        sleep(sleepValue);
      }
      catch(InterruptedException e)
      {
        log.info("[InterruptedException] Mechanism [{}] was interrupted. terminating...", this.getMechanismName());
        return;
      }
    }
  }

  @Override
public String toString()
  {
    String str = "\nMechanism: name=[" + this.mechanismName + "]\n   description=[" + this.description + "]";
    str += "\n   timestepSize=["+timestepSize+"]";
    str += "\n   lastUpdate=["+lastUpdate+"]";
    str += "\n   timestep=["+timestep+"]";
    str += "\n   triggerEvent=["+triggerEvent+"]";
    str += "\n   condition=["+condition+"]";
    str += "\n   authorizationActions=["+authorizationAction+"]";
    str += "\n   executeActions=["+executeAsyncActions+"]";
    return str;
  }
}