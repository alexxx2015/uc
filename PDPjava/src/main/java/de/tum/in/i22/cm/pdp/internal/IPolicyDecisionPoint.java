package de.tum.in.i22.cm.pdp.internal;

import java.util.ArrayList;

public interface IPolicyDecisionPoint
{
  // PDP exported methods
  public Decision notifyEvent(Event event);
  
  public boolean  deployPolicy(String filename);
  public boolean  revokePolicy(String policyName);
  
  public ArrayList<String> listDeployedMechanisms();
  
}
