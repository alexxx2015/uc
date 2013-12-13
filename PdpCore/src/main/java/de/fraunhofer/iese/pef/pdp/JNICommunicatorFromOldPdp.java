package de.fraunhofer.iese.pef.pdp;

import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.pdp.pipcacher.PipCacherImpl;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.basic.KeyBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;


public class JNICommunicatorFromOldPdp {
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
		IPipCacher2Pip _pipHandler = new PipHandler();
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
		int ret=-1;
		Boolean b=_pip.eval(KeyBasic.keyfromString(predicate),(IEvent) event);
		if (b==null) ret=-1;
		if (b==false) ret=0;
		if (b==true) ret=1;
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
//		Boolean b=_pip.eval(KeyBasic.keyfromString(predicate),(IEvent) event);
//		if (b==null) ret=-1;
//		if (b==false) ret=0;
//		if (b==true) ret=1;
		return ret;

	}
	
	public String initialRepresentation(String container)
	{
		return "BLUB"; 		
	}
	
	public String initialRepresentation(String container, String initialDataID)
	{
		return "bla";
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
		return 0;
	}
	
	
	
	//<eval type="XPATH"> danasjknsajknsjkncsjkacnasdjk </eval>

	
	public int eval(String a, String b){
		return (containerRefinesData(a,b));
	}

	public String init(String container)
	{
		return initialRepresentation(container); 		
	}
	
	public String init(String container, String initialDataID)
	{
		return initialRepresentation(container, initialDataID);
	}

}
