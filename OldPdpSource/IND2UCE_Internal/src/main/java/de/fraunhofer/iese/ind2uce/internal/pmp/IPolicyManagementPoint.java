package de.fraunhofer.iese.ind2uce.internal.pmp;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.fraunhofer.iese.ind2uce.internal.pdp.IPolicyDecisionPoint;
import de.fraunhofer.iese.ind2uce.internal.pep.IPolicyEnforcementPoint;
import de.fraunhofer.iese.ind2uce.internal.pip.IPolicyInformationPoint;
import de.fraunhofer.iese.ind2uce.internal.pxp.IPolicyExecutionPoint;

public interface IPolicyManagementPoint extends Remote
{
  public int registerPDP(IPolicyDecisionPoint pdpInstance) throws RemoteException;
  public int registerPIP(IPolicyInformationPoint pipInstance) throws RemoteException;
  public int registerPEP(IPolicyEnforcementPoint pepInstance) throws RemoteException;
  public int registerPXP(IPolicyExecutionPoint pxpInstance) throws RemoteException;
  
  public IPolicyDecisionPoint    lookupPDP() throws RemoteException;
  public IPolicyEnforcementPoint lookupPEP() throws RemoteException;
  public IPolicyExecutionPoint   lookupPXP() throws RemoteException;
  
  public IPolicyEnforcementPoint lookupPEP(String actionName) throws RemoteException;
  public IPolicyExecutionPoint   lookupPXP(String executeName) throws RemoteException;
}
