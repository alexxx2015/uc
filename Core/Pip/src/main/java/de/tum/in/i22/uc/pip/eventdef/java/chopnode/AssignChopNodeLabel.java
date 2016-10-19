package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

public class AssignChopNodeLabel {
	
	private String leftSide;
	private String operation;
	private String[] operands;
	private String sourceId;

	public AssignChopNodeLabel(String labelString) {
		// Form 1: v1 = v2 op v3
		// Form 2: v1 = op v2
		// Form 3: v1 = -(v2)
		
		String[] c = labelString.split("\\|");
		if(c.length >= 1) labelString = c[0];
		if(c.length >= 2) sourceId = c[1];
		
		String[] leftAndRight = labelString.split(" = ");
		String rightSide;
		if (leftAndRight.length == 2) {
			leftSide = leftAndRight[0];
			rightSide = leftAndRight[1];
		} else {
			rightSide = labelString;
		}
		
		// try to split with binary operand from {+,-,*,/,%,?,^,||,&&}
		String[] opernds = rightSide.split("( ([\\+\\-\\*\\/\\%\\?\\^]|(\\|\\|)|(&&)) )");
		if (opernds.length == 2) {
			operands = opernds;
			operation = rightSide.replace(opernds[0] + " ", "").replace(" " + opernds[1], "");
		} else {
			// rip off last component (Form 2)
			String operand = rightSide.substring(rightSide.lastIndexOf(" ") + 1);
			if (operand.equals(rightSide)) {
				// this is probably Form 3
				operation = rightSide.substring(0, 1);
				operands = new String[] { rightSide.substring(2, rightSide.length()-1) };
			} else {
				if(rightSide.toLowerCase().startsWith("checkcast")){
					String[] s = rightSide.split(" ");
					if(s.length >= 1)
						operation = s[0];
					if(s.length >= 2)
						operands = new String[]{s[1]};
				}
				else{
				operation = rightSide.substring(0, rightSide.length() - operand.length() - 1);
				operands = new String[] { operand };
				}
			}
		}
		
	}
	
	public String getLeftSide() {
		return leftSide;
	}
	
	public String getOperation() {
		return operation;
	}

	public String[] getOperands() {
		return operands;
	}
	
}
