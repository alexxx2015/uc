package de.fraunhofer.iese.pef.pdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.fraunhofer.iese.pef.pdp.internal.Constants;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Param;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.pdp.pipcacher.PipCacherImpl;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;


public class JNICommunicatorFromOldPdp {
	private static Logger _logger = Logger.getLogger(JNICommunicatorFromOldPdp.class);
	private static JNICommunicatorFromOldPdp _instance;
	private static IPdpEngine2PipCacher _pip;
	
	private JNICommunicatorFromOldPdp()
	{}
	
	public static JNICommunicatorFromOldPdp getInstance()
	{
		if(_instance==null){
			_instance=new JNICommunicatorFromOldPdp();
			initializeLocalPip();
		}
		return _instance;
	}

	/*
	 * we use a local PIP invoked via function call rather than via GPB
	 */
	private	static void initializeLocalPip(){
		_logger.info("Initialize Local PIP...");
		IPipCacher2Pip _pipHandler = new PipHandler(0);
		_pip=new PipCacherImpl(_pipHandler);
	}

	
	/**************************
	 * 
	 * PIP functionalities
	 * 
	 *************************/
	
	/* return
	 * -1 -> error
	 * 0 -> false
	 * 1 -> true
	 */
	public int evaluatePredicate(String predicate, Event event)
	{
		
		Map<String, String> map = new HashMap<String, String>();
		IEvent e= new EventBasic(event.getEventAction(), map);
		ArrayList<Param<?>> list = event.getParams();
		for (Param<?> p : list){
			if (p.getType()==Constants.PARAMETER_TYPE_STRING) {
				map.put(p.getName(), (String) p.getValue());
			} else if (p.getType()==Constants.PARAMETER_TYPE_INT){
				map.put(p.getName(), ""+ ((Integer)p.getValue()));
			} else {
				//TODO: add parsing for other type of parameters
				_logger.debug("Ignoring parameter cause it's not of type String nor int");
			}
		}
		_logger.trace("evaluatePredicate: ["+predicate+"]");
		int ret=-1;
		Boolean b=_pip.evaluatePredicateSimulatingNextState(e,predicate);
		_logger.error("evaluatePredicateSimulatingNextState("+e+","+predicate+") b="+b);
		if (b==null) ret=-1;
		else if (b==false) ret=0;
		else if (b==true) ret=1;
		
		_logger.trace("evaluatePredicate: ["+predicate+"] = "+ret);
		return ret;
	}
	
	/* return
	 * -1 -> error
	 * 0 -> false
	 * 1 -> true
	 */
	public int containerRefinesData(String container, String dataID)
	{
		int ret=0;
//		Boolean b=_pip.
//		if (b==null) ret=-1;
//		if (b==false) ret=0;
//		if (b==true) ret=1;
		return ret;

	}
	
	public String initialRepresentation(String container)
	{
		return "initialRepresentationForContainer["+container+"]"; 		
	}
	
	public String initialRepresentation(String container, String initialDataID)
	{
		return "initialRepresentationForContainerWithInitialDataID["+container+","+initialDataID+"]";
	}
		
	
	/**************************
	 * 
	 * External evaluated functionalities
	 * 
	 *************************/
	/* return
	 * -1 -> error
	 * 0 -> false
	 * 1 -> true
	 */
	public int evalOperator(String predicateType, String predicate, Event event)
	{
		_logger.trace("evalOperator type="+predicateType+", predicate=["+predicate+"]");
		return 0;
	}
	
	public int pxpExecuteAction(String name, int synchronous, List<Param<?>> params, Event event)
	{
		_logger.trace("executeAction ["+name+"], sync=["+synchronous+"], params=["+params+"]");
		return 0;
	}
	
	
	
	//<eval type="XPATH"> danasjknsajknsjkncsjkacnasdjk </eval>

	// TODO: these methods are obsolete...
//	public int eval(String a, String b){
//		return (containerRefinesData(a,b));
//	}
//
//	public String init(String container)
//	{
//		return initialRepresentation(container); 		
//	}
//	
//	public String init(String container, String initialDataID)
//	{
//		return initialRepresentation(container, initialDataID);
//	}

}
