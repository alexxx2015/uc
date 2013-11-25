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
	
	public int evaluatePredicate(String a, Event b)
	{
		int ret=0;
		//forward to new PIP
		return ret;
	}
	
	public int eval(String a, String b)
	{
		return 0;
	}
	
	public String init(String initialRepresentation)
	{
		return "BLUB"; 		
	}
	
	public String init(String container, String initialDataID)
	{
		return "bla";
	}
	
}
