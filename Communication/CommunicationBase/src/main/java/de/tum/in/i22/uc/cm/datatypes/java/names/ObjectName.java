package de.tum.in.i22.uc.cm.datatypes.java.names;

public class ObjectName extends ReferenceName {
	
	private String pid;
	private String className;
	private String address;
	
	public ObjectName(String objectName) {
		super(objectName);
		
		String[] comps = objectName.split("\\" + DLM);
		pid = comps[0];
		className = comps[1];
		address = comps[2];
	}
	
	public ObjectName(String pid, String className, String address) {
		this(pid + DLM + className + DLM + address);
	}

	public String getPid() {
		return pid;
	}

	public String getClassName() {
		return className;
	}
	
	@Override
	public String getAddress() {
		return address;
	}
	
	/** Returns the same as getClassName()
	 * 
	 */
	@Override
	public String getClassOrType() {
	    return getClassName();
	}
	
}
