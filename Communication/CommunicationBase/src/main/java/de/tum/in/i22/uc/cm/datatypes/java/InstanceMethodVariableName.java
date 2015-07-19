package de.tum.in.i22.uc.cm.datatypes.java;


public class InstanceMethodVariableName extends JavaName {

	private String pid;
	private String threadId;
	private String className;
	private String objectAddress;
	private String methodName;
	private String varName;
	
	public InstanceMethodVariableName(String methodVarName) {
		super(methodVarName);
		
		String[] comps = methodVarName.split("\\" + DLM);
		pid = comps[0];
		threadId = comps[1];
		className = comps[2];
		objectAddress = comps[3];
		methodName = comps[4];
		varName = comps[5];
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
	
	

}
