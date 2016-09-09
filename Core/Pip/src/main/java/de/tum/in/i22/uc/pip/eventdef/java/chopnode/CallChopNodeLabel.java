package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallChopNodeLabel {

	private String methodName;
	private String[] args;
	private String leftSide;
	private String callee;

	private String label;

	public CallChopNodeLabel(String labelString) {
		this.label = labelString;
		if ("".equals(labelString))
			return;
		
		// compute start and end of parameter brackets
		int start = labelString.indexOf("(");
		int end = labelString.lastIndexOf(")");

		// split left part into method invocation and left assignment
		String leftAndMethod = labelString.substring(0, start);
		String[] leftAndRight = leftAndMethod.split("=");
		String methodCall = "";
		if (leftAndRight.length == 2) {
			leftSide = leftAndRight[0];
			methodCall = leftAndRight[1];
		} else if (leftAndRight.length == 1) {
			methodCall = leftAndRight[0];
		}
		if (!"".equals(methodCall)) {
			leftAndRight = methodCall.split("\\.");
			if (leftAndRight.length == 2) {
				callee = leftAndRight[0];
				methodName = leftAndRight[1];
			} else if (leftAndRight.length == 1) {
				methodName = leftAndRight[0];
			}
		}

		// split parameters into its components
		String p = labelString.substring(start+1, end);
//		String[] params = p.split(",");
		Pattern regex = Pattern.compile("#\\([^)]*\\)|(,)");
		Matcher matcher = regex.matcher(p);
		ArrayList<MatchLocation> matches = new ArrayList<MatchLocation>();
		while (matcher.find()) {
			if (matcher.group(1) != null) {
				matches.add(new MatchLocation(matcher.start(1), matcher.end(1)));
			}
		}
		ArrayList<String> splits = new ArrayList<String>();
		for (int i = 0; i <= matches.size(); i++) {
			start = i == 0 ? 0 : matches.get(i - 1).to;
			end = i == matches.size() ? p.length() : matches.get(i).from;
			splits.add(p.substring(start, end).trim());
		}
		args = splits.toArray(new String[] {});
	}

//	old constructor parser
//	private void CallChopNodeLabell(String labelString) {
//		// Form 1: method(arg1, arg2, ...)
//		// Form 2: caller.method(arg1, arg2, ...)
//		// Form 3: assignee = caller.method(arg1, arg2 ...)
//		// Form 4: assignee = method(arg1, arg2, ...)
//		// There can also be no arguments at all
//		this.label = labelString;
//		if ("".equals(labelString))
//			return;
//
//		String[] leftAndRight = labelString.split(" = ");
//		String methodCall;
//		if (leftAndRight.length == 2) {
//			leftSide = leftAndRight[0];
//			methodCall = leftAndRight[1];
//		} else {
//			methodCall = labelString;
//		}
//
//		leftAndRight = methodCall.split("\\.");
//		String methodWithArgs;
//		if (leftAndRight.length == 2) {
//			callee = leftAndRight[0];
//			methodWithArgs = leftAndRight[1];
//		} else {
//			methodWithArgs = methodCall;
//		}
//
//		methodWithArgs = methodWithArgs.substring(0, methodWithArgs.length() - 1);
//		String[] methodAndArgs = methodWithArgs.split("\\(", 2);
//		methodName = methodAndArgs[0];
//		if (methodAndArgs.length > 1) {
//			// split at ", ", but do not split at "#(, )"
//			String argsString = methodAndArgs[1];
//			if (argsString.length() > 0) {
//				Pattern regex = Pattern.compile("#\\([^)]*\\)|(, )");
//				Matcher matcher = regex.matcher(argsString);
//				ArrayList<MatchLocation> matches = new ArrayList<MatchLocation>();
//				while (matcher.find()) {
//					if (matcher.group(1) != null) {
//						matches.add(new MatchLocation(matcher.start(1), matcher.end(1)));
//					}
//				}
//				ArrayList<String> splits = new ArrayList<String>();
//				for (int i = 0; i <= matches.size(); i++) {
//					int start = i == 0 ? 0 : matches.get(i - 1).to;
//					int end = i == matches.size() ? argsString.length() : matches.get(i).from;
//					splits.add(argsString.substring(start, end));
//				}
//				args = splits.toArray(new String[] {});
//			}
//		}
//	}

	public String[] getArgs() {
		return args;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getLeftSide() {
		return leftSide;
	}

	public String getCallee() {
		return callee;
	}

	@Override
	public String toString() {
		return this.leftSide + "--" + this.callee + "--" + this.methodName + " == " + this.label;
	}

	class MatchLocation {
		int from;
		int to;

		MatchLocation(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
}
