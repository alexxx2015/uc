package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

public class ModifyChopNodeLabel {

	private String rightSide;
	private String array;
	private String arrayIndex;
	private String leftSideObject;
	private String fieldName;
	
	public ModifyChopNodeLabel(String labelString) {
		// Form 1: v2[v3] = v0
		// Form 2: v2.fieldName = v0
		
		String[] leftAndRight = labelString.split(" = ");
		String leftSide;
		if (leftAndRight.length == 2) {
			leftSide = leftAndRight[0];
			rightSide = leftAndRight[1];
		} else {
			leftSide = labelString;
		}
		
		// try to go for Form 2 (object.fieldname)
		String[] objectAndField = leftSide.split("\\.");
		if (objectAndField.length == 2) {
			leftSideObject = objectAndField[0];
			fieldName = objectAndField[1];
		}
		
		String[] arrayAndIndex = leftSide.split("\\[");
		if (arrayAndIndex.length == 2) {
			array = arrayAndIndex[0];
			arrayIndex = arrayAndIndex[1].substring(0, arrayAndIndex[1].length()-1);
		}
	}

	public String getRightSide() {
		return rightSide;
	}

	public String getArray() {
		return array;
	}

	public String getArrayIndex() {
		return arrayIndex;
	}

	public String getLeftSideObject() {
		return leftSideObject;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	
}
