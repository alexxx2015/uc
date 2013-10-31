package de.fraunhofer.iese.pef.pdp.rmiServer;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;



public class pdpRMIserver 
{
  private static IPolicyDecisionPoint lpdp,lpdp2;
  private static Registry reg;
  private final static String pdpRMIobjectName="pdpRMI";
  
  private final static int SERVER_PORT = 4041;
  private final static String BINDING_HOSTNAME = "192.168.244.128";//"fw-pretschner1.informatik.tu-muenchen.de";
  
   
  static private void getch(){
	  try {
		System.in.read();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
  }
  
  
  public static int stopRMIserver()
  {
    try
    {
      reg.unbind(pdpRMIobjectName);
    }
    catch(AccessException e)
    {
      System.out.println("Unbinding pdpRMI object resulted in access exception!");
      e.printStackTrace();
      return 1;
    }
    catch(NotBoundException e)
    {
      System.out.println("Unbinding pdpRMI object resulted in NotBoundException!");
      e.printStackTrace();
      return 1;
    }
    catch(RemoteException e)
    {
      System.out.println("Unbinding pdpRMI object resulted in RemoteException!");
      e.printStackTrace();
      return 1;
    }
    
    try
    {
      UnicastRemoteObject.unexportObject(reg,  true);
    }
    catch(NoSuchObjectException e)
    {
      System.out.println("Error while unexporting RMI registry (NoSuchObjectException)");
      e.printStackTrace();
      return 1;
    }
    
    return 0;
  }
  
	public static void main(String[] args) throws RemoteException
	{
	  int port=9983;
	  System.setProperty("java.rmi.server.hostname", BINDING_HOSTNAME);
	  try
	  {
	    port=Integer.parseInt(args[0]);
	    System.out.println("Using port " + port);
	  }
	  catch(Exception e)
	  {
	    System.out.println("Error parsing port from command line arguments. Using port 9983...");
	  }

		System.out.println("Retrieving rmiPDP instance...");
		
	
	lpdp=PolicyDecisionPoint.getInstance();
    if(lpdp==null) System.out.println("Could not retrieve PDP instance!!"); 
    else System.out.println("PDP instance retrieved");
    
		if(System.getSecurityManager()==null)
		{
			System.setSecurityManager(
				new RMISecurityManager()
				{
					public void checkConnect(String host, int port) {}
					public void checkConnect(String host, int port, Object context) {}
					public void checkAccept(String host, int port) {}
				}
			);
		}
		
		System.out.println("[pdpRMI] Retrieving registry...");
					
		//reg=LocateRegistry.getRegistry(port);
		//if (reg==null) System.out.println("no registry found");
		reg=LocateRegistry.createRegistry(port);
		
		for (String str : reg.list()){
			System.out.println("REG.LIST: "+str);
		}
				
		boolean bound=false;
		for(int i=0; !bound && i<2; i++)
		{
			try
			{
//				System.out.println("Gonna unexport lpdp...");
//				UnicastRemoteObject.unexportObject(lpdp, true);
				System.out.println("...done! Now create lpdp2...");
				lpdp2= (IPolicyDecisionPoint) UnicastRemoteObject.exportObject(lpdp, SERVER_PORT);
				System.out.println("...done! now export lpdpd2...");
				reg.rebind(pdpRMIobjectName, lpdp2);
				for (String str : reg.list()){
					System.out.println("REG.LIST: "+str);
				}
						
				bound=true;
				System.out.println("[pdpRMI] <" + pdpRMIobjectName + "> bound to registry, port " + port);
			}
			catch (RemoteException e) 
			{
				System.out.println("[pdpRMI] Rebinding " + pdpRMIobjectName + " failed (" + e.getMessage() +"), retrying...");
				reg=LocateRegistry.createRegistry(port);
				System.out.println("[pdpRMI] Registry started on port: " + port);
			} 
		}
		System.out.println("[pdpRMI] pdpRMIserver ready");
	}
}
