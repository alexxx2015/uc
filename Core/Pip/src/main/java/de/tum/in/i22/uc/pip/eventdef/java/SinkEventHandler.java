package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.containers.SinkSourceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.names.BasicJavaName;
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
		String calleeObjectAddress = null;
		String calleeObjectClass = null;
		String calleeMethod = null;
		JSONObject contextInformation = null;
		CallChopNodeLabel chopLabel = null;
		String chopLabelStr;
		String[] methodArgTypes = null;
		JSONArray methodArgTypesJSON;
		JSONArray methodArgValuesJSON;
		JSONArray methodArgAddressesJSON;
		String[] methodArgAddresses = null;
		String[] methodArgValues = null;
		String sinkParam = null;
		String sinkId = null;
		JSONArray dependsOnSources = null;

		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			calleeObjectAddress = getParameterValue("calledObjectAddress");
			calleeObjectClass = getParameterValue("calledObjectClass");
			calleeMethod = getParameterValue("calledMethod");
			contextInformation = (JSONObject) new JSONParser().parse(getParameterValue("contextInformation"));
			chopLabelStr = getParameterValue("chopLabel");
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
			sinkParam = getParameterValue("sinkParam");
			sinkId = getParameterValue("sinkId");
			dependsOnSources = (JSONArray) new JSONParser().parse(getParameterValue("dependsOnSources"));

			methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
			methodArgTypes = new String[methodArgTypesJSON.size()];
			methodArgTypesJSON.toArray(methodArgTypes);

			methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
			methodArgAddresses = new String[methodArgAddressesJSON.size()];
			methodArgAddressesJSON.toArray(methodArgAddresses);

			methodArgValuesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgValues"));
			methodArgValues = new String[methodArgValuesJSON.size()];
			methodArgValuesJSON.toArray(methodArgValues);
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String sinkAddress = "";
		if ((Integer.parseInt(sinkParam) > 0) && (methodArgAddresses.length  >= Integer.parseInt(sinkParam))){
			sinkAddress = methodArgAddresses[Integer.parseInt(sinkParam)-1];
		}		
		if (sinkParam.toLowerCase().equals("ret")) {
			sinkParam = chopLabel.getLeftSide();
		} else if ((Integer.valueOf(sinkParam) > 0) && (chopLabel.getArgs() != null)
				&& (chopLabel.getArgs().length > 0)) {
			sinkParam = chopLabel.getArgs()[Integer.valueOf(sinkParam) - 1];
		}
		
		IName sinkVarName = JavaNameFactory.createSinkName(pid, threadId, parentClass, parentMethod, chopLabel.getCallee(), sinkId, calleeObjectAddress);		
		IContainer sinkContainer = _informationFlowModel.getContainer(sinkVarName);
		if(sinkContainer == null){
			sinkContainer = new SinkSourceContainer(pid,threadId,sinkId,calleeObjectAddress);			
			_informationFlowModel.addName(sinkVarName, sinkContainer, false);
		}
		
		//Copy all data items from the source container to the sink container
		Collection<IName> _names = _informationFlowModel.getAllNames();
		Iterator<IName> _namesIt = _names.iterator();
		List<SourceSinkName> sourceSinkName = new LinkedList<SourceSinkName>();
		while (_namesIt.hasNext()) {
			IName _next = _namesIt.next();
			if (_next instanceof SourceSinkName) {
				SourceSinkName s = (SourceSinkName) _next;
				for (int i = 0; i < dependsOnSources.size(); i++) {
					if (s.getSourceSinkId().equals(dependsOnSources.get(i).toString())) {
						sourceSinkName.add(s);
					}
				}
			}
		}

		if (sourceSinkName.size() > 0) {
			for(SourceSinkName s : sourceSinkName)
				_informationFlowModel.copyData(s, sinkVarName);
			
			if (calleeMethod.toLowerCase().equals("put") && (calleeObjectClass.toLowerCase().equals("java.util.hashmap")
					|| calleeObjectClass.toLowerCase().equals("java.util.map"))) {
				String key = methodArgValues[0];
				String value = methodArgValues[1];

				IName covn = new BasicJavaName(pid, calleeObjectAddress, calleeObjectClass, key);
				IContainer c = _informationFlowModel.getContainer(covn);
				if(c == null)
					_informationFlowModel.addName(covn, sinkContainer);
				else
					_informationFlowModel.addData(_informationFlowModel.getData(covn), c);
			} 
		}

		return _messageFactory.createStatus(EStatus.OKAY);
//		return new JavaPipStatus(EStatus.OKAY, srcName, scopeData);
	}
}
//IContainer sinkContainer = addContainerIfNotExists(sinkVarName, calleeObjectClass, calleeObjectAddress);
//IName calleeObjectName = new ObjectName(pid, calleeObjectClass, calleeObjectAddress);
//IContainer calleeObjectContainer = addContainerIfNotExists(calleeObjectName, calleeObjectClass,calleeObjectAddress);
// insertArguments(chopLabel.getArgs(), methodArgTypes,
// methodArgAddresses, pid, threadId, parentClass, parentObjectAddress,
// parentMethod,
// callerObjectClass, callerObjectAddress, calledMethod,
// callerObjectClassIsInstrumented ? null
// : callerObjectContainer);