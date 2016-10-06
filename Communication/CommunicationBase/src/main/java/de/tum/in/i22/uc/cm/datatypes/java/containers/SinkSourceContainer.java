package de.tum.in.i22.uc.cm.datatypes.java.containers;

import java.util.HashMap;
import java.util.Map;

public class SinkSourceContainer extends JavaContainer{
	
	private Map<String,String> contextInformation;

	protected SinkSourceContainer(String id) {
		super(id);
		// TODO Auto-generated constructor stub
		this.contextInformation = new HashMap<String,String>();
	}
	
	public SinkSourceContainer(String pid, String tid, String id, String memAddress){
		this(pid+DLM+tid+DLM+id+DLM+memAddress);
	}
	
	public void addCtxInfo(String key, String value){
		this.contextInformation.put(key, value);
	}

}
