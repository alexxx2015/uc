package main;

import helper.IPIPCommunication;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PIPPDPExampleErrorExample
{
  public static void main(String[] args)
  {
    // IPIPCommunication pipHandler=new PIPHandler();
    IPIPCommunication pipHandler=PIPHandler.getInstance();
    pipHandler.initializePIP();

    //System.out.println("printing model");
    //System.out.println(pipHandler.printModel());

    // int taintTag = 5132;
    int taintTag=Integer.MAX_VALUE;
    int uid=10025;
    Hashtable<Integer, String> map=new Hashtable<Integer, String>();

    // so geht es nicht:
    for(int marking : getTaintMarkings(taintTag))
    {
      map.put(marking, pipHandler.init(uid, String.valueOf(marking)));
      System.out.println("init " + uid + " x " + marking + "\t:= " + map.get(marking));
    }

    System.out.println("");
    for(int marking : getTaintMarkings(taintTag))
    {
      System.out.println("eval " + uid + " x " + marking + "\t = " + pipHandler.getDataIDbyRepresentation(uid, String.valueOf(marking), false));
    }

    System.out.println("");
    for(int marking : getTaintMarkings(taintTag))
    {
      System.out.println("get  " + uid + " x " + marking + "\t == " + map.get(marking) + "\t => "
          + (pipHandler.eval(uid, String.valueOf(marking), map.get(marking), false) == 1));
    }
    
    // so scheint es zu gehen???
    /*
    for(int marking : getTaintMarkings(taintTag))
    {
      map.put(marking, pipHandler.init(uid, String.valueOf(marking)));
      System.out.println("init " + uid + " x " + marking + "\t:= " + map.get(marking));
      System.out.println("eval " + uid + " x " + marking + "\t = " + pipHandler.getDataIDbyRepresentation(uid, String.valueOf(marking), false));
      System.out.println("get  " + uid + " x " + marking + "\t == " + map.get(marking) + "\t => "
          + (pipHandler.eval(uid, String.valueOf(marking), map.get(marking), false) == 1));
    }
    */

  }
  
  // only for extraction the android taint markings: 1,2,4,8,16,32,64,...
  public static List<Integer> getTaintMarkings(int taintTag)
  {
    List<Integer> collection=new ArrayList<Integer>();
    while(taintTag > 0)
    {
      int taintMarking=Integer.lowestOneBit(taintTag);
      collection.add(taintMarking);
      taintTag=taintTag - taintMarking;
    }
    return collection;
}


}
