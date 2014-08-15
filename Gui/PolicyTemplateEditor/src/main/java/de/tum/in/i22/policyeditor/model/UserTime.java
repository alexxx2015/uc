package de.tum.in.i22.policyeditor.model;

import javax.swing.JComboBox;

public class UserTime extends JComboBox{

	public static final String TIME = "#TI[time]TI#";
	public static final String START = "#TI[";
	public static final String END = "]TI#";
	
	private static String[] comboTypes = { "seconds", "minutes", "hours", "days", "years" };
	
	public UserTime(String selectedOption) {
		super(comboTypes);
		this.setSelectedItem(selectedOption);
	}
	
	public static void setMenuItems(String[] items){
		comboTypes = items;
	}
	
	public String getStringRepresentation(){
		String data = "";
		data += START + this.getSelectedItem() + END;
		return data;
	}
	
	public String toString(){
		return this.getStringRepresentation();
	}
	
	public String getValue(){
		return (String) this.getSelectedItem();
	}
	
}
