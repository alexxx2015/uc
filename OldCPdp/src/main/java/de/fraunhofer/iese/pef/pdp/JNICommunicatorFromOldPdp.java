package de.fraunhofer.iese.pef.pdp;

import java.util.List;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Param;

// this is a copy of the appropriate class in PdpCore! just for the testCase
public class JNICommunicatorFromOldPdp
{
  private static JNICommunicatorFromOldPdp _instance;

  // private static IPdpEngine2PipCacher _pip;

  private JNICommunicatorFromOldPdp()
  {
  }

  public static JNICommunicatorFromOldPdp getInstance()
  {
    if(_instance == null)
    {
      _instance=new JNICommunicatorFromOldPdp();
      initializeLocalPip();
    }
    return _instance;
  }

  /*
   * we use a local PIP invoked via function call rather than via GPB
   */
  private static void initializeLocalPip()
  {
//    IPipCacher2Pip _pipHandler=new PipHandler();
//    _pip=new PipCacherImpl(_pipHandler);
  }

  /**************************
   * 
   * PIP functionalities
   * 
   *************************/

  /*
   * return -1 -> error 0 -> false 1 -> true
   */
  public int evaluatePredicate(String predicate, Event event)
  {
    System.out.println("evaluatePredicate: [" + predicate + "]");
    int ret=-1;
//    Boolean b=_pip.eval(KeyBasic.keyfromString(predicate), (IEvent)event);
//    if(b == null) ret=-1;
//    if(b == false) ret=0;
//    if(b == true) ret=1;
    return ret;
  }


//	public int evaluatePredicate(String predicate, Event event) {
//		int result=-1;
//		
//		System.err.println("Saving PIP current state");
//		if (pipModel.push()) {
//			System.err.println("Updating PIP semantics with current event (" + (event==null?"null":event.getEventAction())+")");
//			pipSemantics.processEvent(event, pipModel);
//			System.err.println("Evaluate predicate in new updated state (" + predicate+")");
//			result = evaluatePredicateInSimulatedState(predicate);
//			System.err.print("Restoring PIP previous state...");
//			pipModel.pop();
//			System.err.println("done!");
//			return result;
//		}
//		System.err.println("Failed! Stack not empty!");
//		
//		return -1;
//	}

  /*
   * return -1 -> error 0 -> false 1 -> true
   */
  public int containerRefinesData(String container, String dataID)
  {
    int ret=0;
    // Boolean b=_pip.eval(KeyBasic.keyfromString(predicate),(IEvent) event);
    // if (b==null) ret=-1;
    // if (b==false) ret=0;
    // if (b==true) ret=1;
    return ret;

  }

  public String initialRepresentation(String container)
  {
    return "initialRepresentationForContainer[" + container + "]";
  }

  public String initialRepresentation(String container, String initialDataID)
  {
    return "initialRepresentationForContainerWithInitialDataID[" + container + "," + initialDataID + "]";
  }

  /**************************
   * 
   * External evaluated functionalities
   * 
   *************************/
  /*
   * return -1 -> error 0 -> false 1 -> true
   */
  public int evalOperator(String predicateType, String predicate, Event event)
  {
    System.out.println("evalOperator type=" + predicateType + ", predicate=[" + predicate + "]");
    return 0;
  }

  public int pxpExecuteAction(String name, int synchronous, List<Param<?>> params, Event event)
  {
    System.out.println("executeAction [" + name + "], sync=[" + synchronous + "], params=[" + params + "]");
    return 0;
  }

  // <eval type="XPATH"> danasjknsajknsjkncsjkacnasdjk </eval>

  // TODO: these methods are obsolete...
//  public int eval(String a, String b)
//  {
//    return(containerRefinesData(a, b));
//  }
//
//  public String init(String container)
//  {
//    return initialRepresentation(container);
//  }
//
//  public String init(String container, String initialDataID)
//  {
//    return initialRepresentation(container, initialDataID);
//  }

}
