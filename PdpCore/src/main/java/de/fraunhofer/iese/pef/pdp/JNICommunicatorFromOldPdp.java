package de.fraunhofer.iese.pef.pdp;

import de.fraunhofer.iese.pef.pdp.internal.Event;


public class JNICommunicatorFromOldPdp {
	private static JNICommunicatorFromOldPdp _instance;
	
	private JNICommunicatorFromOldPdp()
	{}
	
	public static JNICommunicatorFromOldPdp getInstance()
	{
		if(_instance==null) _instance=new JNICommunicatorFromOldPdp(); 
		return _instance;
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
		int ret=0;
		//forward to new PIP
		return ret;
	}
	
	/* return
	 * -1 -> error
	 * 0 -> false
	 * 1 -> true
	 */
	public int containerRefinesData(String container, String dataID)
	{
		return 0;
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
	

}
