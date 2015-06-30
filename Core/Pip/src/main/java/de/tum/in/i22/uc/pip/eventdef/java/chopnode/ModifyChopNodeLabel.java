package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifyChopNodeLabel {

	private String rightSide;
	private String array;
	private String arrayIndex;
	private String fieldOwner;
	private String fieldName;
	
	public ModifyChopNodeLabel(String labelString) {
		// Form 1: v2[v3] = v0
		// Form 2: v2.fieldName = v0
		// Form 3: package.class.fieldName = v0
		
		String[] leftAndRight = labelString.split(" = ");
		String leftSide;
		if (leftAndRight.length == 2) {
			leftSide = leftAndRight[0];
			rightSide = leftAndRight[1];
		} else {
			leftSide = labelString;
		}
		
		// try to go for Form 2 or 3 (ow.ner.fieldname)
		// last component is fieldname, the ones before make up the owner class/object
		String[] components = leftSide.split("\\.");
		if (components.length >= 2) {
			List<String> compList = new ArrayList<String>(Arrays.asList(components));
			fieldName = compList.remove(compList.size()-1);
			fieldOwner = String.join(".", compList);
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

	public String getFieldOwner() {
		return fieldOwner;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	
}
