package de.tum.in.i22.uc.cm.datatypes.java.names;

import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName.Type;

public class StaticMethodVariableName extends JavaName {

    private String pid;
    private String threadId;
    private String className;
    private String methodName;
    private String varName;

	private String sourceSinkId;
	private Type type;
	
    public StaticMethodVariableName(String methodVarName) {
	super(methodVarName);

	String[] comps = methodVarName.split("\\" + DLM);
	pid = comps[0].trim();
	threadId = comps[1].trim();
	className = comps[2].trim();
	methodName = comps[3].trim();
	varName = comps[4].trim();
    }

    public StaticMethodVariableName(String pid, String threadId, String className, String methodName, String varName) {
	this(pid + DLM + threadId + DLM + className + DLM + methodName + DLM + varName);
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
