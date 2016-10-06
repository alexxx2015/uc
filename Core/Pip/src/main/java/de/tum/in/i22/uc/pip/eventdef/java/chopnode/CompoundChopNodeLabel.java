package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

public class CompoundChopNodeLabel {
	
	private String argument;
	private String sourceId;
	
	public CompoundChopNodeLabel(String labelString) {
		
		String[] c = labelString.split("\\|");
		if(c.length >= 1) labelString = c[0];
		if(c.length >= 2) sourceId = c[1];
		
		String[] parts = labelString.split(" ");
		if (parts.length == 2) {
			argument = parts[1];
		}
	}
	
	public String getArgument() {
		return argument;
	}

}
