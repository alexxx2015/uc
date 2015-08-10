package de.tum.in.i22.uc.pip.eventdef.java;

import static de.tum.in.i22.uc.cm.datatypes.java.NameKeys.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ReferenceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ValueContainer;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayElementName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayName;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceFieldName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticFieldName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class AfterEnforcementEventHandler extends JavaEventHandler {

    @SuppressWarnings("serial")
    @Override
    protected IStatus update() {
	Map<String, Set<Map<String, String>>> modelSubset = null;

	try {
	    Type modelSubsetType = new TypeToken<Map<String, Set<Map<String, String>>>>() {
	    }.getType();
	    modelSubset = new Gson().fromJson(getParameterValue("modelSubset"), modelSubsetType);
	} catch (ParameterNotFoundException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	modelSubset.get(OBJECTS).forEach(objectProps -> {
	    String pid = objectProps.get(PID);
	    String className = objectProps.get(CLASSNAME);
	    String address = objectProps.get(ADDRESS);

	    // clear object container
	    // remove field names and aliases to owner objects
		IName objectName = new ObjectName(pid, className, address);
		IContainer objectContainer = _informationFlowModel.getContainer(objectName);
		if (objectContainer != null) {
		    _informationFlowModel.emptyContainer(objectContainer);

		    Set<InstanceFieldName> fieldNames = new HashSet<>(_informationFlowModel
			    .getAllNames(InstanceFieldName.class));
		    fieldNames.forEach(fieldName -> {
			if (fieldName.getPid().equals(pid) && fieldName.getClassName().equals(className)
				&& fieldName.getObjectAddress().equals(address)) {
			    IContainer fieldContainer = _informationFlowModel.getContainer(fieldName);
			    _informationFlowModel.removeAlias(fieldContainer, objectContainer);
			    _informationFlowModel.removeName(fieldName);
			    // value type fieldContainers get deleted automatically because they only have one name
			}
		    });
		}
	    });

	modelSubset.get(ARRAYS).forEach(arrayProps -> {
	    String pid = arrayProps.get(PID);
	    String type = arrayProps.get(TYPE);
	    String address = arrayProps.get(ADDRESS);

	    // clear array data
	    // remove array element names
	    // remove array element aliases

		IName arrayName = new ArrayName(pid, type, address);
		IContainer arrayContainer = _informationFlowModel.getContainer(arrayName);
		if (arrayContainer != null) {
		    _informationFlowModel.emptyContainer(arrayContainer);

		    Set<ArrayElementName> aeNames = new HashSet<>(_informationFlowModel
			    .getAllNames(ArrayElementName.class));
		    aeNames.forEach(aeName -> {
			if (aeName.getPid().equals(pid) && aeName.getType().equals(type)
				&& aeName.getAddress().equals(address)) {
			    IContainer aeContainer = _informationFlowModel.getContainer(aeName);
			    _informationFlowModel.removeAlias(aeContainer, arrayContainer);
			    _informationFlowModel.removeName(aeName);
			    // value type aeContainers get deleted automatically because they only have one name
			}
		    });
		}

	    });

	modelSubset.get(ARRAY_ELEMENTS).forEach(arrayElemProps -> {
	    String pid = arrayElemProps.get(PID);
	    String type = arrayElemProps.get(TYPE);
	    String address = arrayElemProps.get(ADDRESS);
	    int index = Integer.parseInt(arrayElemProps.get(INDEX));
	    
	    // here only value type containers come in question -> just remove the name
	    
	    IName arrayElemName = new ArrayElementName(pid, type, address, index);
	    _informationFlowModel.removeName(arrayElemName);

	});

	modelSubset.get(INSTANCE_FIELDS).forEach(fieldProps -> {
	    String pid = fieldProps.get(PID);
	    String className = fieldProps.get(CLASSNAME);
	    String objectAddress = fieldProps.get(OBJECT_ADDRESS);
	    String field = fieldProps.get(FIELD_NAME);

	    // here only value type containers come in question -> just remove the name
	    
	    IName fieldName = new InstanceFieldName(pid, className, objectAddress, field);
	    _informationFlowModel.removeName(fieldName);
	});

	modelSubset.get(STATIC_FIELDS).forEach(fieldProps -> {
	    String pid = fieldProps.get(PID);
	    String className = fieldProps.get(CLASSNAME);
	    String field = fieldProps.get(FIELD_NAME);
	    
	    // here only value type containers come in question -> just remove the name

	    IName fieldName = new StaticFieldName(pid, className, field);
	    _informationFlowModel.removeName(fieldName);
	});

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
