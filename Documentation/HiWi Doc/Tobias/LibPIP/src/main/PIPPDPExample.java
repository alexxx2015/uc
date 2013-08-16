package main;

import helper.IPIPCommunication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;

import de.fraunhofer.iese.pef.pdp.PolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Constants;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Param;

public class PIPPDPExample
{

  private static IPolicyDecisionPoint lpdp;
  
  public static StringBuffer readFile(String filename) throws IOException
  {
    File file=new File(filename);
    StringBuffer contents=new StringBuffer();
    BufferedReader reader=null;
    reader=new BufferedReader(new FileReader(file));
    String text=null;
    while((text=reader.readLine()) != null)
      contents.append(text).append(System.getProperty("line.separator"));
    reader.close();
    return contents;
  }  
  
  public static void main(String[] args)
  {
    System.out.println("Starting combined PIP-PDP test...");
    System.out.println("Initiating PDP instance...");
    try
    {
      lpdp=PolicyDecisionPoint.getInstance();
      if(lpdp==null) {System.out.println("Could not retrieve PDP instance!!"); return;} 
      System.out.println("PDP instance retrieved");
    }
    catch(RemoteException e)
    {
      System.out.println("RemoteException occured: " + e.getMessage());
      System.exit(1);
    }
    
    System.out.println("[jniPDP]: Start PDP");
    int startret=-1;
    try
    {
      startret=lpdp.pdpStart();
      System.out.println("jniPDP: PDP started with result=["+startret+"]");
      if(startret==1) 
      {
        System.out.println("Error starting PDP!");
        return;
      }
    }
    catch(Exception e)
    {
      System.out.println("Error starting PDP...");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    
    //IPIPCommunication pipHandler=new PIPHandler();
    IPIPCommunication pipHandler=PIPHandler.getInstance();
    pipHandler.initializePIP();

    try
    {
      System.out.println("jniPDP: deployed mechanisms:\n##" + lpdp.listDeployedMechanismsJNI() + "##");
    }
    catch(RemoteException e)
    {
      System.out.println("RemoteException occured: " + e.getMessage());
    }

    
    try
    {
      Event event1=new Event("action2", true, System.currentTimeMillis());
      event1.addParam(new Param<String>("val2", "value2", Constants.PARAMETER_TYPE_STRING));

      System.in.read();
      System.out.println("Sending event " + event1);
      Decision pdpResponse=lpdp.pdpNotifyEventJNI(event1);
      System.out.println(pdpResponse.toString());
    }
    catch(IOException e)
    {
      System.out.println("Exception occured: " + e.getMessage());
    }

    System.out.println("printing model");
    System.out.println(pipHandler.printModel());

  }

}
