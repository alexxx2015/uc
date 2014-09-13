package de.tum.in.i22.policyeditor.model;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class UserNumber extends JSpinner{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4397512059014471847L;
	public static final String NUMBER = "#NUNO[number]NU#";
	public static final String START = "#NUNO[";
	public static final String END = "]NU#";
	public static final String COUNTER = "NO";
	
	private int order;
	
	public UserNumber(String selectedNumber, int order) {
		super(new SpinnerNumberModel(1, 1, 9999, 1)); //default value,lower bound,upper bound,increment by);
		this.order = order;
		try{
			int value = Integer.parseInt(selectedNumber);
			if(value <= 0)
				value = 1;
			this.setValue(value);
		}
		catch(IllegalArgumentException e){
			this.setValue(1);
		}
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
