package de.fraunhofer.iese.ind2uce.internal.pep;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPolicyEnforcementPoint extends Remote
{
  public int handlePEPsubscribe(String request, int unsubscribe) throws RemoteException;
}
