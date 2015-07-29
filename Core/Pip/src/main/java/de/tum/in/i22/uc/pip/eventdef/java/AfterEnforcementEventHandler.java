package de.tum.in.i22.uc.pip.eventdef.java;

import static de.tum.in.i22.uc.cm.datatypes.java.NameKeys.*;

import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayElementName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayName;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceFieldName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticFieldName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class AfterEnforcementEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {
	Map<String, Set<Map<String, String>>> modelSubset = null;

	try {
	    modelSubset = (JSONObject) new JSONParser().parse(getParameterValue("modelSubset"));
	} catch (ParameterNotFoundException | ParseException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	modelSubset.get(OBJECTS).forEach(objectProps -> {
	    String pid = objectProps.get(PID);
	    String className = objectProps.get(CLASSNAME);
	    String oldAddress = objectProps.get(ADDRESS);
	    String newAddress = objectProps.get(NEW_ADDRESS);
	    replaceAddressInNamesAndContainers(null, pid, className, oldAddress, newAddress);
	});

	modelSubset.get(ARRAYS).forEach(arrayProps -> {
	    String pid = arrayProps.get(PID);
	    String type = arrayProps.get(TYPE);
	    String oldAddress = arrayProps.get(ADDRESS);
	    String newAddress = arrayProps.get(NEW_ADDRESS);
	    replaceAddressInNamesAndContainers(null, pid, type, oldAddress, newAddress);
	});

	modelSubset.get(ARRAY_ELEMENTS).forEach(arrayElemProps -> {
	    String pid = arrayElemProps.get(PID);
	    String type = arrayElemProps.get(TYPE);
	    String address = arrayElemProps.get(ADDRESS);
	    int index = Integer.parseInt(arrayElemProps.get(INDEX));

	    IName arrayElemName = new ArrayElementName(pid, type, address, index);
	    IName arrayName = new ArrayName(pid, type, address);
	    IContainer arrayContainer = _informationFlowModel.getContainer(arrayName);
	    if (arrayContainer != null) {
		IContainer arrayElemContainer = _informationFlowModel.getContainer(arrayElemName);
		_informationFlowModel.emptyContainer(arrayElemContainer);
	    } else {
		_informationFlowModel.removeName(arrayElemName);
		_informationFlowModel.removeName(arrayName);
	    }

	});

	modelSubset.get(INSTANCE_FIELDS).forEach(fieldProps -> {
	    String pid = fieldProps.get(PID);
	    String className = fieldProps.get(CLASSNAME);
	    String objectAddress = fieldProps.get(OBJECT_ADDRESS);
	    String field = fieldProps.get(FIELD_NAME);

	    IName fieldName = new InstanceFieldName(pid, className, objectAddress, field);
	    IName fieldOwnerName = new ObjectName(pid, className, objectAddress);
	    IContainer fieldOwnerContainer = _informationFlowModel.getContainer(fieldOwnerName);
	    if (fieldOwnerContainer != null) {
		IContainer fieldContainer = _informationFlowModel.getContainer(fieldName);
		_informationFlowModel.emptyContainer(fieldContainer);
	    } else {
		_informationFlowModel.removeName(fieldName);
		_informationFlowModel.removeName(fieldOwnerName);
	    }
	});

	modelSubset.get(STATIC_FIELDS).forEach(fieldProps -> {
	    String pid = fieldProps.get(PID);
	    String className = fieldProps.get(CLASSNAME);
	    String field = fieldProps.get(FIELD_NAME);

	    IName fieldName = new StaticFieldName(pid, className, field);
	    IContainer fieldContainer = _informationFlowModel.getContainer(fieldName);
	    _informationFlowModel.emptyContainer(fieldContainer);
	});

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
