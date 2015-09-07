package de.tum.in.i22.uc.cm.datatypes.java.names;

public class ArrayElementName extends JavaName {
	
	private String pid;
	private String type;
	private String address;
	private int index;
	
	public ArrayElementName(String objectName) {
		super(objectName);
		
		String[] comps = objectName.split("\\" + DLM);
		pid = comps[0];
		type = comps[1];
		address = comps[2];
		index = Integer.valueOf(comps[3]);
	}
	
	public ArrayElementName(String pid, String type, String address, int index) {
		this(pid + DLM + type + DLM + address + DLM + index);
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
	
	public int getIndex() {
		return index;
	}
	
}
