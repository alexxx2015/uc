package de.tum.in.i22.cm.pdp.internal;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public interface IPolicyDecisionPoint
{
  // PDP exported methods
  public Decision notifyEvent(Event event);
  
  public boolean  deployPolicy(String filename);
  public boolean  revokePolicy(String policyName);
  public boolean  revokePolicy(String policyName, String mechName);

  public HashMap<String, ArrayList<Mechanism>> listDeployedMechanisms();
  
  public boolean registerPxp(IPxpSpec pxp);
  
}
