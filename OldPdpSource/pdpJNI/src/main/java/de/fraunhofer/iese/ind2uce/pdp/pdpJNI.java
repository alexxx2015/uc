package de.fraunhofer.iese.ind2uce.pdp;

import java.io.IOException;

import de.fraunhofer.iese.ind2uce.internal.pdp.Decision;
import de.fraunhofer.iese.ind2uce.internal.pdp.Event;
import de.fraunhofer.iese.ind2uce.internal.pdp.IPolicyDecisionPoint;

public class pdpJNI
{
  private static IPolicyDecisionPoint lpdp;
  
  public static void main(String args[]) throws IOException, InterruptedException 
  {
    System.out.println("Initiating PDP instance...");
    lpdp=PolicyDecisionPoint.getInstance();
    if(lpdp==null) {System.out.println("Could not retrieve PDP instance!!"); return;} 
    System.out.println("PDP instance retrieved");
    
    System.out.println("[jniPDP]: Start PDP");
    int startret=-1;
    try
    {
      startret=lpdp.pdpStart();
      System.out.println("jniPDP: PDP started with result=["+startret+"]");
    }
    catch(Exception e)
    {
      System.out.println("Error starting PDP...");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    
    if(startret==1) 
    {
      System.out.println("Error starting PDP!");
      return;
    }
    
    int ret=0;
    // do something...
    //
    
    System.out.println(lpdp.listDeployedMechanismsJNI());
    Event e = new Event("bliblablub", true);
    e.addStringParameter("param", "value");
    Decision d = lpdp.pdpNotifyEventJNI(e);
    System.out.println(d);
    
    System.in.read();
    System.in.read();
    System.in.read();
    System.in.read();
    System.in.read();
    System.in.read();
    System.out.println("Stopping pdp...");
    ret=lpdp.pdpStop();
    System.out.println("Native PDP stopping finished ["+ret+"]");

    System.exit(0);
  }

}







