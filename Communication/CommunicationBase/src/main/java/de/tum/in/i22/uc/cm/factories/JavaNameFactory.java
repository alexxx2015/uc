package de.tum.in.i22.uc.cm.factories;

import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceFieldName;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceMethodVariableName;
import de.tum.in.i22.uc.cm.datatypes.java.names.JavaName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticFieldName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticMethodVariableName;
import de.tum.in.i22.uc.cm.settings.Settings;

public class JavaNameFactory {

    public static JavaName createLocalVarName(String pid, String threadId, String className, String objectAddress,
	    String methodName, String varName) {
	if (objectAddress == null || objectAddress.equals(Settings.getInstance().getJavaNull())) {
	    return new StaticMethodVariableName(pid, threadId, className, methodName, varName);
	} else {
	    return new InstanceMethodVariableName(pid, threadId, className, objectAddress, methodName, varName);
	}
    }
    
    public static JavaName createFieldName(String pid, String className, String objectAddress,
	    String fieldName) {
	if (objectAddress == null || objectAddress.equals(Settings.getInstance().getJavaNull())) {
	    return new StaticFieldName(pid, className, fieldName);
	} else {
	    return new InstanceFieldName(pid, className, objectAddress, fieldName);
	}
    }

}
