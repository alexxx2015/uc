package de.tum.in.i22.policyeditor.model;

public class UserClass {

	public static final String CLASS = "#CL[class]CL#";
	public static final String START = "#CL[";
	public static final String END = "]CL#";
	
	
	private String _class;
	
	public UserClass(String _class){
		this._class = _class;
	}
	
	public String getStringRepresentation(){
		String data = "";
		data += START + this._class + END;
		return data;
	}
	
	public String toString(){
		return this.getStringRepresentation();
	}
	
	public String getValue(){
		return this._class;
	}
	
}
