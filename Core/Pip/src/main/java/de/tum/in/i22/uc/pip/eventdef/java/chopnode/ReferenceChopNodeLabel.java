package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReferenceChopNodeLabel {
	
	private String leftSide;
	private String array;
	private String arrayIndex;
	private String fieldOwner;
	private String fieldName;
	
	private String sourceId;
	
	public ReferenceChopNodeLabel(String labelString) {
		// Form 1: v0 = v2[v3]
		// Form 2: v0 = v2.fieldName
		// Form 3: v0 = package.class.fieldName
		
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
		
		// try to go for Form 2 or 3 (ow.ner.fieldname)
		// last component is fieldname, the ones before make up the owner class/object
		String[] components = rightSide.split("\\.");
		if (components.length >= 2) {
			List<String> compList = new ArrayList<String>(Arrays.asList(components));
			fieldName = compList.remove(compList.size()-1);
			fieldOwner = String.join(".", compList);
		}
		
		String[] arrayAndIndex = rightSide.split("\\[");
		if (arrayAndIndex.length == 2) {
			array = arrayAndIndex[0];
			arrayIndex = arrayAndIndex[1].substring(0, arrayAndIndex[1].length()-1);
		}
	}

	public String getLeftSide() {
		return leftSide;
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
