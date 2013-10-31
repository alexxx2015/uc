package de.fraunhofer.iese.ind2uce.internal.pip;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPolicyInformationPoint extends Remote
{
  // value1 -> representation;  value2 -> data;  returns boolean flag whether representation refines data
  // value1 -> name of context; value2 -> param; returns boolean flag whether context is satisfied
  int eval(String value1, String value2) throws RemoteException;
  
  // method -> deployContext or initialRepresentation
  String init(String method, String param) throws RemoteException;
}
