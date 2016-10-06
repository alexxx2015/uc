package de.tum.in.i22.uc.cm.datatypes.java.names;

public class ArrayName extends ReferenceName {
	
	private String pid;
	private String type;
	private String address;
	
	public ArrayName(String objectName) {
		super(objectName);
		
		String[] comps = objectName.split("\\" + DLM);
		pid = comps[0].trim();
		type = comps[1].trim();
		address = comps[2].trim();
	}
	
	public ArrayName(String pid, String type, String address) {
		this(pid + DLM + type + DLM + address);
	}

	public String getPid() {
		return pid;
	}

	public String getType() {
		return type;
	}
	
	@Override
	public String getAddress() {
		return address;
	}

	/** Returns the same as getType().
	 * 
	 */
	@Override
	public String getClassOrType() {
	    return getType();
	}
	
	
}
