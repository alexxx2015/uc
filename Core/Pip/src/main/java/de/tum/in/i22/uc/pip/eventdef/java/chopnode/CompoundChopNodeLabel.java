package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

public class CompoundChopNodeLabel {
	
	private String argument;
	
	public CompoundChopNodeLabel(String labelString) {
		String[] parts = labelString.split(" ");
		if (parts.length == 2) {
			argument = parts[1];
		}
	}
	
	public String getArgument() {
		return argument;
	}

}
