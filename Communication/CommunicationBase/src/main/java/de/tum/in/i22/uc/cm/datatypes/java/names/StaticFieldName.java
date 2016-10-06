package de.tum.in.i22.uc.cm.datatypes.java.names;

public class StaticFieldName extends JavaName {
	
	private String pid;
	private String className;
	private String fieldName;
	
	public StaticFieldName(String staticFieldName) {
		super(staticFieldName);
		
		String[] comps = staticFieldName.split("\\" + DLM);
		pid = comps[0].trim();
		className = comps[1].trim();
		fieldName = comps[2].trim();
	}
	
	public StaticFieldName(String pid, String className, String fieldName) {
		this(pid + DLM + className + DLM + fieldName);
	}

	public String getPid() {
		return pid;
	}

	public String getClassName() {
		return className;
	}

	public String getFieldName() {
		return fieldName;
	}

}
