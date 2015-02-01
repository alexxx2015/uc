package de.tum.in.i22.policyeditor.model;

import javax.swing.JComboBox;

public class UserSubject extends JComboBox{

	public static final String SUBJECT = "#SU[subject]SU#";
	public static final String START = "#SU[";
	public static final String END = "]SU#";
	
	public static final String SUBJECT_REG_EX = "\"[^\"]#SU[subject]SU#\"";
	public static final String START_REG_EX = "\"#SU[";
	public static final String END_REG_EX = "]SU#\"";
	
	private static String[] comboTypes = { "me" };
	
	public UserSubject(String selectedOption) {
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
