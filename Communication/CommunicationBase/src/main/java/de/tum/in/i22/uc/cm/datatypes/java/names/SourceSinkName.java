package de.tum.in.i22.uc.cm.datatypes.java.names;


public class SourceSinkName extends JavaName {
	public enum Type{SINK,SOURCE};
	
	private String pid;
	private String threadId;
	private String parentClass;
	private String parentMethod;
	private String sourceObjectAddress;
	private String varName;
	private String sourceSinkId;
	private Type type;
	
	public SourceSinkName(String methodVarName) {
		super(methodVarName);
		
		String[] comps = methodVarName.split("\\" + DLM);
		pid = comps[0];
		threadId = comps[1];
		parentClass = comps[2];
		parentMethod = comps[3];
		varName = comps[4];
		sourceObjectAddress = comps[5];
	}

	public SourceSinkName(String pid, String threadId, String parentClass, String parentMethod, String varName, String objectAddress) {
		this(pid + DLM + threadId + DLM + parentClass + DLM + parentMethod + DLM + varName+ DLM +objectAddress);
	}

	public String getPid() {
		return pid;
	}

	public String getThreadId() {
		return threadId;
	}

	public String getClassName() {
		return parentClass;
	}

	public String getObjectAddress() {
		return sourceObjectAddress;
	}

	public String getMethodName() {
		return parentMethod;
	}

	public String getVarName() {
		return varName;
	}
	
	public String getSourceSinkId(){
		return this.sourceSinkId;
	}
	public Type getType(){
		return this.type;
	}
	
	public void setSourceSinkId(String p_sourcesinkid, Type p_type){
		this.sourceSinkId = p_sourcesinkid;
		this.type = p_type;
	}	
}
