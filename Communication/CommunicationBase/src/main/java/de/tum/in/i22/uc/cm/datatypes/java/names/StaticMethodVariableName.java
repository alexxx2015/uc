package de.tum.in.i22.uc.cm.datatypes.java.names;

public class StaticMethodVariableName extends JavaName {

    private String pid;
    private String threadId;
    private String className;
    private String methodName;
    private String varName;

    public StaticMethodVariableName(String methodVarName) {
	super(methodVarName);

	String[] comps = methodVarName.split("\\" + DLM);
	pid = comps[0];
	threadId = comps[1];
	className = comps[2];
	methodName = comps[3];
	varName = comps[4];
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

}
