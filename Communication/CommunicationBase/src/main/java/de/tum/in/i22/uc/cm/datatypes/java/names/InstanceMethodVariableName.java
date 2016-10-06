package de.tum.in.i22.uc.cm.datatypes.java.names;

import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName.Type;

public class InstanceMethodVariableName extends JavaName {

	private String pid;
	private String threadId;
	private String className;
	private String objectAddress;
	private String methodName;
	private String varName;
	
	private String sourceSinkId;
	private Type type;
	
	public InstanceMethodVariableName(String methodVarName) {
		super(methodVarName);
		
		String[] comps = methodVarName.split("\\" + DLM);
		pid = comps[0].trim();
		threadId = comps[1].trim();
		className = comps[2].trim();
		objectAddress = comps[3].trim();
		methodName = comps[4].trim();
		varName = comps[5].trim();
	}

	public InstanceMethodVariableName(String pid, String threadId, String className, String objectAddress, String methodName, String varName) {
		this(pid + DLM + threadId + DLM + className + DLM + objectAddress + DLM + methodName + DLM + varName);
	}

	public String getPid() {
		return pid;
	}

	public String getThreadId() {
		return threadId;
	}

	public String getClassName() {
		return className;
	}

	public String getObjectAddress() {
		return objectAddress;
	}

	public String getMethodName() {
		return methodName;
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
