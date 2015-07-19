package de.tum.in.i22.uc.cm.datatypes.java;

public class ArrayName extends JavaName {
	
	private String pid;
	private String type;
	private String address;
	
	public ArrayName(String objectName) {
		super(objectName);
		
		String[] comps = objectName.split("\\" + DLM);
		pid = comps[0];
		type = comps[1];
		address = comps[2];
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

	public String getAddress() {
		return address;
	}
	
}
