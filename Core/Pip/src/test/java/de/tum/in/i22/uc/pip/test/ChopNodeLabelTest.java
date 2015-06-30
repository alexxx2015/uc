package de.tum.in.i22.uc.pip.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import de.tum.in.i22.uc.pip.eventdef.java.chopnode.AssignChopNodeLabel;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CompoundChopNodeLabel;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ModifyChopNodeLabel;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;
import de.tum.in.i22.uc.thrift.types.TAny2Any.AsyncProcessor.informRemoteDataFlow;
import de.tum.in.i22.uc.thrift.types.TAny2Any.AsyncProcessor.newInitialRepresentation;



public class ChopNodeLabelTest {

	@Test
	public void testChopNodes() {
		//testCallChopNodes();
		//testAssignChopNodes();
		//testCompoundChopNodes();
		testReferenceChopNodes();
		testModifyChopNodes();
	}
	
	public void testCallChopNodes() {
		String[] inputs = new String[] { 
				"v6 = p2.length()",
				"v10 = p1.substring(v6, v8)",
				"v8.add(v12)",
				"v37 = v24.split(#(fewfwe), v5)",
				"valueOf(p2)",
				"this.writeInt(#(File unzip ...))"};
		
		for (String input : inputs) {
			CallChopNodeLabel cnl = new CallChopNodeLabel(input);
			System.out.println();
			System.out.println("input = " + input);
			System.out.println("assignee = " + (cnl.getLeftSide() != null ? cnl.getLeftSide() : "null"));
			System.out.println("caller = " + (cnl.getCaller() != null ? cnl.getCaller() : "null"));
			System.out.println("methodName = " + (cnl.getMethodName() != null ? cnl.getMethodName() : "null"));
			System.out.println("args = " + (cnl.getArgs() != null ? formatStringArray(cnl.getArgs()) : "null"));
		}
		
	}
	
	public void testAssignChopNodes() {
		
		String[] inputs = new String[] { 
				"v36 = CHECKCAST v35",
				"v14 = v5 - #(35 l)",
				"v7 = CONVERT I to B v6",
				"v1 = v5 + v17",
				"v14 = #(1) - v5",
				"v15 = #(23) - #(32)",
				"v5 = -(v4)",
				"v4 = p1 ? #(4)",
				"v3 = p1 ^ p1",
				"v4 = v3 || v3",
				"v5 = v4 && v4"
				};
		
		for (String input : inputs) {
			AssignChopNodeLabel cnl = new AssignChopNodeLabel(input);
			System.out.println();
			System.out.println("input = " + input);
			System.out.println("assignee = " + (cnl.getLeftSide() != null ? cnl.getLeftSide() : "null"));
			System.out.println("operation = " + (cnl.getOperation() != null ? cnl.getOperation() : "null"));
			System.out.println("operands = " + (cnl.getOperands() != null ? formatStringArray(cnl.getOperands()) : "null"));
		}
	}
	
	public void testCompoundChopNodes() {
		String[] inputs = new String[] { 
				"return v35",
				"return p2"
				};
		
		for (String input : inputs) {
			CompoundChopNodeLabel cnl = new CompoundChopNodeLabel(input);
			System.out.println();
			System.out.println("input = " + input);
			System.out.println("argument = " + (cnl.getArgument() != null ? cnl.getArgument() : "null"));
		}
	}
	
	public void testReferenceChopNodes() {
				
		String[] inputs = new String[] { 
				"v25 = v16[#(1)]",
				"v18 = this.path",
				"v11 = v6[v10]",
				"v6 = this.data",
				"v26 = test.DataMover.staticx"};
		
		for (String input : inputs) {
			ReferenceChopNodeLabel cnl = new ReferenceChopNodeLabel(input);
			System.out.println();
			System.out.println("input = " + input);
			System.out.println("leftside = " + (cnl.getLeftSide() != null ? cnl.getLeftSide() : "null"));
			System.out.println("array = " + (cnl.getArray() != null ? cnl.getArray() : "null"));
			System.out.println("arrayindex = " + (cnl.getArrayIndex() != null ? cnl.getArrayIndex() : "null"));
			System.out.println("rightsideobject = " + (cnl.getFieldOwner() != null ? cnl.getFieldOwner() : "null"));
			System.out.println("fieldname = " + (cnl.getFieldName() != null ? cnl.getFieldName() : "null"));
		}
	}
	
	public void testModifyChopNodes() {
				
		String[] inputs = new String[] { 
				"p2[#(0)] = v11",
				"this.path = #( )",
				"v6[v10] = v11",
				"test.DataMover.staticx = v14"};
		
		for (String input : inputs) {
			ModifyChopNodeLabel cnl = new ModifyChopNodeLabel(input);
			System.out.println();
			System.out.println("input = " + input);
			System.out.println("rightside = " + (cnl.getRightSide() != null ? cnl.getRightSide() : "null"));
			System.out.println("array = " + (cnl.getArray() != null ? cnl.getArray() : "null"));
			System.out.println("arrayindex = " + (cnl.getArrayIndex() != null ? cnl.getArrayIndex() : "null"));
			System.out.println("leftsideobject = " + (cnl.getFieldOwner() != null ? cnl.getFieldOwner() : "null"));
			System.out.println("fieldname = " + (cnl.getFieldName() != null ? cnl.getFieldName() : "null"));
		}
	}

	private static String formatStringArray(String[] strings) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");
		for (String s : strings) {
			stringBuilder.append("\"" + s + "\"");
			if (s != strings[strings.length - 1]) {
				stringBuilder.append(", ");
			}
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
