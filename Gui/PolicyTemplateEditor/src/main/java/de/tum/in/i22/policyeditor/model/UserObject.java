package de.tum.in.i22.policyeditor.model;

public class UserObject {

	public static final String CLASS = "#OB[object]OB#";
	public static final String START = "#OB[";
	public static final String END = "]OB#";
	
	
	private String obj;
	
	public UserObject(String object){
		this.obj = object;
	}
	
	public String getStringRepresentation(){
		String data = "";
		data += START + this.obj + END;
		return data;
	}
	
	public String toString(){
		return this.getStringRepresentation();
	}
	
	public String getValue(){
		return this.obj;
	}
	
}
