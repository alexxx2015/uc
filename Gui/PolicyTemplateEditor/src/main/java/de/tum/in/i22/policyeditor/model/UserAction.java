package de.tum.in.i22.policyeditor.model;

import javax.swing.JComboBox;

public class UserAction extends JComboBox{

	public static final String ACTION = "#AC[action]AC#";
	public static final String START = "#AC[";
	public static final String END = "]AC#";
	
	
	
	private static String[] comboTypes = { "copy", "delete", "modify", "distribute" };
	
	public UserAction(String selectedOption) {
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
