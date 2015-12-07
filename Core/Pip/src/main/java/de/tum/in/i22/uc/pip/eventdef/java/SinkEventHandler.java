package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.datatypes.java.names.SourceSinkName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class SinkEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		String threadId = null;
		String pid = null;
		String parentObjectAddress = null;
		String parentClass = null;
		String parentMethod = null;
		String calledObjectAddress = null;
		String calledObjectClass = null;
		String calledMethod = null;
		JSONObject contextInformation = null;
		CallChopNodeLabel chopLabel = null; String chopLabelStr;
		String[] methodArgTypes = null;
		String[] methodArgAddresses = null;		
		String sinkParam = null;
		String sinkId = null;
		JSONArray dependsOnSources = null;

		try {
		    threadId = getParameterValue("threadId");
		    pid = getParameterValue("processId");
		    parentObjectAddress = getParameterValue("parentObjectAddress");
		    parentClass = getParameterValue("parentClass");
		    parentMethod = getParameterValue("parentMethod");
		    calledObjectAddress = getParameterValue("calledObjectAddress");
		    calledObjectClass = getParameterValue("calledObjectClass");
		    calledMethod = getParameterValue("calledMethod");
		    contextInformation = (JSONObject) new JSONParser().parse(getParameterValue("contextInformation"));
		    chopLabelStr = getParameterValue("chopLabel");
		    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
			sinkParam = getParameterValue("sinkParam");
			sinkId = getParameterValue("sinkId");
			dependsOnSources = (JSONArray) new JSONParser().parse(getParameterValue("dependsOnSources"));
		    
		    JSONArray methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
		    methodArgTypes = new String[methodArgTypesJSON.size()];
		    methodArgTypesJSON.toArray(methodArgTypes);
		    
		    JSONArray methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
		    methodArgAddresses = new String[methodArgAddressesJSON.size()];
		    methodArgAddressesJSON.toArray(methodArgAddresses);
		} catch (ParameterNotFoundException | ClassCastException e) {
		    _logger.error(e.getMessage());
		    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// if caller object class is a system class, get its container to add
		// method parameters to it
		
		IContainer calleeObjectContainer = null;
		IName callerObjectName = new ObjectName(pid, calledObjectClass, calledObjectAddress);
		calleeObjectContainer = addContainerIfNotExists(callerObjectName, calledObjectClass, calledObjectAddress);

		IName calleeObjectVarName = JavaNameFactory.createSinkName(pid, threadId, parentClass, parentObjectAddress,
			parentMethod, chopLabel.getCallee(), sinkId);
		_informationFlowModel.addName(calleeObjectVarName, calleeObjectContainer, false);
		
		Collection<IName> _names = _informationFlowModel.getAllNames();
		Iterator<IName> _namesIt = _names.iterator();
		while(_namesIt.hasNext()){
			IName _next = _namesIt.next();
			if(_next instanceof SourceSinkName){
				SourceSinkName s = (SourceSinkName)_next;
				for(int i = 0; i < dependsOnSources.size(); i++){
					if(s.getSourceSinkId().equals(dependsOnSources.get(i).toString())){
						_informationFlowModel.copyData(s, calleeObjectVarName);
						_informationFlowModel.addData(_informationFlowModel.getData(s), calleeObjectContainer);
					}	
				}
			}
		}

//		insertArguments(chopLabel.getArgs(), methodArgTypes, methodArgAddresses, pid, threadId, parentClass, parentObjectAddress, parentMethod,
//			callerObjectClass, callerObjectAddress, calledMethod, callerObjectClassIsInstrumented ? null
//				: callerObjectContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
//		return new JavaPipStatus(EStatus.OKAY, srcName, scopeData);
	}
}
