package de.tum.in.i22.uc.pdp.internal;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionDescriptionStore
{
  private HashMap<String, ArrayList<EventMatch>> eventMatchList =null;
  private HashMap<String, ArrayList<Mechanism>>  mechanismList  =null;

  private static ActionDescriptionStore          instance       =null;
  
  private ActionDescriptionStore()
  {
    this.eventMatchList = new HashMap<String, ArrayList<EventMatch>>();
    this.mechanismList = new HashMap<String, ArrayList<Mechanism>>();
  }
  
  public static ActionDescriptionStore getInstance()
  {
    if(instance==null) instance=new ActionDescriptionStore();
    return instance;
  }
  
  public boolean addEventMatch(EventMatch e)
  {
    ArrayList<EventMatch> eventMatchList = this.eventMatchList.get(e.getAction());
    if(eventMatchList==null) eventMatchList = new ArrayList<EventMatch>();
    eventMatchList.add(e);
   
    this.eventMatchList.put(e.getAction(), eventMatchList);
    return true;
  }
  
  public void addMechanism(Mechanism m)
  {
    ArrayList<Mechanism> mechanismList = this.mechanismList.get(m.getTriggerEvent().getAction());
    if(mechanismList==null) mechanismList = new ArrayList<Mechanism>();
    mechanismList.add(m);
    
    this.mechanismList.put(m.getTriggerEvent().getAction(), mechanismList);
  }
  
  public ArrayList<EventMatch> getEventList(String eventAction)
  {
    return this.eventMatchList.get(eventAction);
  }
  
  public ArrayList<Mechanism> getMechanismList(String eventAction)
  {
    return this.mechanismList.get(eventAction);
  }
  
  public boolean removeMechanism(String eventAction){	  	
	   return this.mechanismList.remove(eventAction) == null;
  }
}
