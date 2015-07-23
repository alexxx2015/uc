package de.tum.in.i22.uc.cm.datatypes.java.names;

public class InstanceFieldName extends JavaName {
	
	private String pid;
	private String className;
	private String objectAddress;
	private String fieldName;
	
	public InstanceFieldName(String instanceFieldName) {
		super(instanceFieldName);
		
		String[] comps = instanceFieldName.split("\\" + DLM);
		pid = comps[0];
		className = comps[1];
		objectAddress = comps[2];
		fieldName = comps[3];
	}
	
	public InstanceFieldName(String pid, String className, String objectAddress, String fieldName) {
		this(pid + DLM + className + DLM + objectAddress + DLM + fieldName);
	}

	public String getPid() {
		return pid;
	}

	public String getClassName() {
		return className;
	}

	public String getObjectAddress() {
		return objectAddress;
	}

	public String getFieldName() {
		return fieldName;
	}

}
