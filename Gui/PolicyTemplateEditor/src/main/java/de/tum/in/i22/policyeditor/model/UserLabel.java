package de.tum.in.i22.policyeditor.model;

import javax.swing.JLabel;

public class UserLabel extends JLabel{

	public UserLabel(String data){
		super(data);
	}
	
	public String getStringRepresentation(){
		String data = "";
		data += this.getText();
		return data;
	}
	
	public String toString(){
		return this.getStringRepresentation();
	}
	
}
