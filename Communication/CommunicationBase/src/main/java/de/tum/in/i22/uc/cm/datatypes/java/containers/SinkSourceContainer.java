package de.tum.in.i22.uc.cm.datatypes.java.containers;

public class SinkSourceContainer extends JavaContainer{

	protected SinkSourceContainer(String id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	public SinkSourceContainer(String pid, String tid, String id, String memAddress){
		this(pid+DLM+tid+DLM+id+DLM+memAddress);
	}

}
