package de.tum.in.i22.uc.pip.eventdef.java.chopnode;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallChopNodeLabel {

	private String methodName;
	private String[] args;
	private String leftSide;
	private String caller;
	
	public CallChopNodeLabel(String labelString) {
		// Form 1: method(arg1, arg2, ...)
		// Form 2: caller.method(arg1, arg2, ...)
		// Form 3: assignee = caller.method(arg1, arg2 ...)
		// Form 4: assignee = method(arg1, arg2, ...)
		// There can also be no arguments at all
		
		
		String[] leftAndRight = labelString.split(" = ");
		String methodCall;
		if (leftAndRight.length == 2) {
			leftSide = leftAndRight[0];
			methodCall = leftAndRight[1];
		} else {
			methodCall = labelString;
		}
		
		leftAndRight = methodCall.split("\\.");
		String methodWithArgs;
		if (leftAndRight.length == 2) {
			caller = leftAndRight[0];
			methodWithArgs = leftAndRight[1];
		} else {
			methodWithArgs = methodCall;
		}
		
		methodWithArgs = methodWithArgs.substring(0, methodWithArgs.length()-1);
		String[] methodAndArgs = methodWithArgs.split("\\(", 2);
		methodName = methodAndArgs[0];
		if (methodAndArgs.length > 1) {
			// split at ", ", but do not split at "#(, )"
			String argsString = methodAndArgs[1];
			if (argsString.length() > 0) {
				Pattern regex = Pattern.compile("#\\([^)]*\\)|(, )");
				Matcher matcher = regex.matcher(argsString);
				ArrayList<MatchLocation> matches = new ArrayList<MatchLocation>();
				while (matcher.find()) {
					if(matcher.group(1) != null) {
						matches.add(new MatchLocation(matcher.start(1), matcher.end(1)));
					}
				}
				ArrayList<String> splits = new ArrayList<String>();
				for (int i = 0; i <= matches.size(); i++) {
					int start = i == 0 ? 0 : matches.get(i-1).to;
					int end = i == matches.size() ? argsString.length() : matches.get(i).from;
					splits.add(argsString.substring(start, end));
				}
				args = splits.toArray(new String[]{});
			}
		}
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getLeftSide() {
		return leftSide;
	}
	
	public String getCaller() {
		return caller;
	}
	
	class MatchLocation {
		int from;
		int to;
		MatchLocation(int from, int to) {
			this.from = from; this.to = to;
		}
	}
	
}
