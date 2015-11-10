package de.tum.in.i22.uc.pip.eventdef.java;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.data.SourceData;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

//CallMethodEventHandler
public class SourceEventHandler extends JavaEventHandler {

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
		String sourceObjectAddress = null;
		String sourceObjectClass = null;
		JSONObject contextInfo = null;
		String[] methodArgTypes = null;
		String[] methodArgAddresses = null;
		CallChopNodeLabel chopLabel = null;
		String sourceParam = null;
		String sourceId = null;

		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			calleeObjectAddress = getParameterValue("calleeObjectAddress");
			calleeObjectClass = getParameterValue("calleeObjectClass");
			calleeMethod = getParameterValue("calleeMethod");
			sourceObjectAddress = getParameterValue("returnObjectAddress");
			sourceObjectClass = getParameterValue("returnObjectClass");
			contextInfo = (JSONObject) new JSONParser().parse(getParameterValue("contextInformation"));
			//e.g. v23 = v8.readLine()
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
			sourceParam = getParameterValue("sourceParam");
			sourceId = getParameterValue("sourceId");
			// JSONArray methodArgTypesJSON = (JSONArray) new
			// JSONParser().parse(getParameterValue("methodArgTypes"));
			// methodArgTypes = new String[methodArgTypesJSON.size()];
			// methodArgTypesJSON.toArray(methodArgTypes);
			// JSONArray methodArgAddressesJSON = (JSONArray) new
			// JSONParser().parse(getParameterValue("methodArgAddresses"));
			// methodArgAddresses = new String[methodArgAddressesJSON.size()];
			// methodArgAddressesJSON.toArray(methodArgAddresses);
			System.out.println("SourceEventHandler: " + chopLabel.toString());
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if caller object class is a system class, get its container to add
		// method parameters to it
		IName calleeObjectName = new ObjectName(pid, calleeObjectClass,
				calleeObjectAddress);
		IContainer calleeObjectContainer = addContainerIfNotExists(calleeObjectName,
				calleeObjectClass, calleeObjectAddress);

		IName calleeObjectVarName = JavaNameFactory.createLocalVarName(pid,
				threadId, parentClass, parentObjectAddress, parentMethod,
				chopLabel.getCallee());
		_informationFlowModel.addName(calleeObjectVarName,
				calleeObjectContainer, false);
		
		if(sourceParam.toLowerCase().equals("ret")){
			sourceParam = chopLabel.getLeftSide();
		} else if ((Integer.valueOf(sourceParam) > 0) && (chopLabel.getArgs() != null) && (chopLabel.getArgs().length > 0)){
			sourceParam = chopLabel.getArgs()[Integer.valueOf(sourceParam) -1];
		}
		
		IName sourceObjectVarName = JavaNameFactory.createSourceName(pid,
				threadId, sourceObjectClass, sourceObjectAddress, parentMethod, sourceParam, sourceId);
		IContainer sourceObjectContainer = addContainerIfNotExists(sourceObjectVarName, sourceObjectClass, sourceObjectAddress);
		IData sourceData = new SourceData(sourceId+"-"+contextInfo.get("path"),System.currentTimeMillis());
		_informationFlowModel.addData(sourceData, sourceObjectContainer);

		// insertArguments(chopLabel.getArgs(), methodArgTypes,
		// methodArgAddresses, pid, threadId, parentClass, parentObjectAddress,
		// parentMethod,
		// calleeObjectClass, calleeObjectAddress, calleeMethod,
		// calleeObjectContainer);//calleeObjectClassIsInstrumented ? null:
		// calleeObjectContainer

		return _messageFactory.createStatus(EStatus.OKAY);
		// return new JavaPipStatus(EStatus.OKAY, srcName, scopeData);
	}

}
