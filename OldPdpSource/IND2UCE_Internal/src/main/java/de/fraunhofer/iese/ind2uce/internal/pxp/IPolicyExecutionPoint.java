package de.fraunhofer.iese.ind2uce.internal.pxp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import de.fraunhofer.iese.ind2uce.internal.pdp.Event;
import de.fraunhofer.iese.ind2uce.internal.pdp.Param;

public interface IPolicyExecutionPoint extends Remote
{
  public int handlePXPexecute(String name, ArrayList<Param<?>> params, Event event) throws RemoteException;
}
