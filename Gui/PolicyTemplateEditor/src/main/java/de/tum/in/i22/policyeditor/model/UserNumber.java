package de.tum.in.i22.policyeditor.model;

import javax.swing.JSpinner;

public class UserNumber extends JSpinner{

	public static final String NUMBER = "#NUNO[number]NU#";
	public static final String START = "#NUNO[";
	public static final String END = "]NU#";
	public static final String COUNTER = "NO";
	
	private int order;
	
	public UserNumber(String selectedNumber, int order) {
		super();
		this.order = order;
		try{
			int value = Integer.parseInt(selectedNumber);
			this.setValue(value);
		}
		catch(IllegalArgumentException e){}
	}
	
	public String getStringRepresentation(){
		String data = "";
		data += START.replace(COUNTER, ""+order) + this.getValue() + END;
		return data;
	}
	
	public String getGenericRepresentation(){
		String data = "";
		data += NUMBER.replace(COUNTER, ""+order);
		return data;
	}
	
	public String toString(){
		return this.getStringRepresentation();
	}
	
}
