package de.tum.in.i22.uc.pip;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import static de.tum.in.i22.uc.cm.datatypes.java.NameKeys.*;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ArrayContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ObjectContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ValueContainer;
import de.tum.in.i22.uc.cm.datatypes.java.data.JavaData;
import de.tum.in.i22.uc.cm.datatypes.java.data.SourceData;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayElementName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ArrayName;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceFieldName;
import de.tum.in.i22.uc.cm.datatypes.java.names.JavaName;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticFieldName;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.core.statebased.StateBasedPredicate;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipStatus;

public class PipHandler extends PipProcessor {
    private static final Logger _logger = LoggerFactory.getLogger(PipHandler.class);

    private final IBasicInformationFlowModel _ifModel;

    private final InformationFlowModelManager _ifModelManager;

    private final PipManager _pipManager;

    // /**
    // * Manages everything related to distributed data flow tracking
    // */
    // private final PipDistributionManager _distributedPipManager;

    public PipHandler() {
	this(new InformationFlowModelManager());
    }

    public PipHandler(InformationFlowModelManager ifmModelManager) {
	super(LocalLocation.getInstance());
	init(new DummyPdpProcessor(), new DummyPmpProcessor());

	_pipManager = new PipManager();
	// _distributedPipManager = new PipDistributionManager();
	_ifModelManager = ifmModelManager;
	_ifModel = _ifModelManager.getBasicInformationFlowModel();

	// initialize data flow according to settings
	update(new EventBasic(Settings.getInstance().getPipInitializerEvent(), null, true));
    }

    @Override
    public boolean evaluatePredicateCurrentState(String predicate) {
	IStateBasedPredicate pred;

	try {
	    pred = StateBasedPredicate.create(predicate, _ifModelManager);
	} catch (InvalidStateBasedFormulaException e) {
	    _logger.warn(e.toString());
	    return false;
	}
	if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
	    _logger.debug(this.toString());
	}
	if (pred == null) {
	    _logger.error("Predicate to be evaluated is null. returning predefined value false. This shouldn't happen, though.");
	    return false;
	}
	try {
	    return pred.evaluate();
	} catch (InvalidStateBasedFormulaException e) {
	    e.printStackTrace();
	    return false;

	}
    }

    @Override
    public Set<IContainer> getContainersForData(IData data) {
	return _ifModel.getContainers(data);
    }

    @Override
    public Set<IData> getDataInContainer(IName containerName) {
	return _ifModel.getData(containerName);
    }

    @Override
    public IStatus update(IEvent event) {
	String eventName = event.getName();
	IEventHandler eventHandler = null;
	IStatus status;

	/*
	 * Get the event handler and perform the information flow update
	 * according to its implemented semantics
	 */

	try {
	    eventHandler = EventHandlerManager.createEventHandler(event);
	} catch (Exception e) {
	    return new StatusBasic(EStatus.ERROR, "Could not instantiate event handler for " + eventName + ", "
		    + e.getMessage());
	}

	if (eventHandler == null) {
	    return new StatusBasic(EStatus.ERROR);
	}

	eventHandler.setEvent(event);
	eventHandler.setInformationFlowModel(_ifModelManager);

	_logger.info(System.lineSeparator() + "Executing PipHandler for " + event);
	status = eventHandler.performUpdate();

	/*
	 * The returned status will tell us whether we have to do some more
	 * work, namely remote data flow tracking and policy shipment
	 */
	if (status.isStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED) && status instanceof DistributedPipStatus) {

	    _distributionManager.dataTransfer(((DistributedPipStatus) status).getDataflow());
	}

	if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
	    _logger.debug(this.toString());
	}

	return status;
    }

    @Override
    public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
	    EConflictResolution flagForTheConflictResolution) {
	return _pipManager.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
    }

    @Override
    public IStatus startSimulation() {
	return _ifModelManager.startSimulation();
    }

    @Override
    public IStatus stopSimulation() {
	return _ifModelManager.stopSimulation();
    }

    @Override
    public boolean isSimulating() {
	return _ifModelManager.isSimulating();
    }

    /**
     * Evaluate the predicate in the state obtained simulating the execution of
     * event.
     *
     * @return the result of the formula
     */
    @Override
    public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
	_logger.info("Saving PIP current state");

	Boolean res = null;

	if (_ifModelManager.startSimulation().getEStatus() == EStatus.OKAY) {
	    _logger.trace("Updating PIP semantics with current event (" + (event == null ? "null" : event.getName())
		    + ")");
	    update(event);
	    _logger.trace("Evaluate predicate in new updated state (" + predicate + ")");
	    res = evaluatePredicateCurrentState(predicate);
	    _logger.trace("Result of the evaluation is " + res);
	    _logger.trace("Restoring PIP previous state...");
	    _ifModelManager.stopSimulation();
	    _logger.trace("done!");
	} else {
	    _logger.error("Failed! Stack not empty!");
	}

	return res;
    }

    @Override
    public boolean hasAllData(Set<IData> data) {
	Set<IData> all = new HashSet<>();
	for (IContainer c : _ifModel.getAllContainers()) {
	    all.addAll(_ifModel.getData(c));
	}
	return all.containsAll(data);
    }

    @Override
    public boolean hasAnyData(Set<IData> data) {
	for (IContainer c : _ifModel.getAllContainers()) {
	    if (_ifModel.getData(c).contains(data)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean hasAllContainers(Set<IName> containers) {
	// TODO
	return false;
    }

    @Override
    public boolean hasAnyContainer(Set<IName> containers) {
	// TODO
	return false;
    }

    @Override
    public IStatus initialRepresentation(IName containerName, Set<IData> data) {
	_logger.debug("initialRepresentation(" + containerName + "," + data + ")");

	IContainer container;
	if ((container = _ifModel.getContainer(containerName)) == null) {
	    _ifModel.addName(containerName, container = new ContainerBasic());
	}

	if (data == null || data.size() == 0) {
	    newInitialRepresentation(containerName);
	} else {
	    _ifModel.addDataTransitively(data, container);
	}

	return new StatusBasic(EStatus.OKAY);
    }

    @Override
    public IData newInitialRepresentation(IName containerName) {
	_logger.debug("newInitialRepresentation(" + containerName + ")");
	IContainer container;
	IData d = new DataBasic();

	if ((container = _ifModel.getContainer(containerName)) == null) {
	    _ifModel.addName(containerName, container = new ContainerBasic());
	}

	_ifModel.addDataTransitively(Collections.singleton(d), container);
	return d;
    }

    @Override
    public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
	// return _distributedPipManager.whoHasData(data, recursionDepth);
	return Collections.emptySet();
    }

    @Override
    public String toString() {
	return _ifModelManager.niceString();
    }

    @Override
    public IData newStructuredData(Map<String, Set<IData>> structure) {
	return _ifModelManager.newStructuredData(structure);
    }

    @Override
    public Map<String, Set<IData>> getStructureOf(IData data) {
	return _ifModelManager.getStructureOf(data);
    }

    @Override
    public Set<IData> flattenStructure(IData data) {
	return _ifModelManager.flattenStructure(data);
    }

    @Override
    public void stop() {
	// TODO Auto-generated method stub

    }

    @Override
    public Map<String, Set<Map<String, String>>> filterModel(Map<String, String> params) {
	
	String pid = params.get("processId");
	String sourceId = params.get("sourceId");
	long minTimeStamp = Long.valueOf(params.get("minTimeStamp"));
	
	Map<String, Set<Map<String, String>>> filteredModel = new HashMap<>();
	filteredModel.put(OBJECTS, new HashSet<Map<String,String>>());
	filteredModel.put(INSTANCE_FIELDS, new HashSet<Map<String,String>>());
	filteredModel.put(STATIC_FIELDS, new HashSet<Map<String,String>>());
	filteredModel.put(ARRAYS, new HashSet<Map<String,String>>());
	filteredModel.put(ARRAY_ELEMENTS, new HashSet<Map<String,String>>());
	
	// collect all containers (with their names) where each name has the specified pid
	// they should also contain data with specified sourceId and being older than minTimeStamp
	Map<IContainer, Set<JavaName>> filteredContainers = new HashMap<IContainer, Set<JavaName>>();
	
	for (IName name : _ifModel.getAllNames()) {
	    if (name instanceof JavaName) {
		JavaName javaName = (JavaName) name;
		if (javaName.getPid().equals(pid)) {
		    IContainer container = _ifModel.getContainer(javaName);
		    for (IData data : _ifModel.getData(container)) {
			if (data instanceof SourceData) {
			    SourceData sourceData = (SourceData)data;
			    if (sourceData.getSourceId().equals(sourceId) && sourceData.getTimeStamp() < minTimeStamp) {
				if (!filteredContainers.keySet().contains(container)) {
				    filteredContainers.put(container, new HashSet<JavaName>());
				}
				filteredContainers.get(container).add(javaName);
				break;
			    }
			}
		    }
		    
		}
	    }
	}

	// filter names for enforceable ones, one name per container is enough to enforce
	// check if container value type -> it should have only ONE name
	// container array type -> always has ONE ArrayName pointing to it
	// container object type -> always has ONE ObjectName pointing to it
	for (Entry<IContainer, Set<JavaName>> containerWithNames : filteredContainers.entrySet()) {
	    IContainer container = containerWithNames.getKey();
	    Set<JavaName> names = containerWithNames.getValue();
	    if (container instanceof ValueContainer) {
		if (!names.isEmpty()) {
		    JavaName name = names.iterator().next();
		    if (name instanceof ArrayElementName) {
			ArrayElementName arrayElementName = (ArrayElementName) name;
			Map<String, String> props = new HashMap<>();
			props.put(PID, arrayElementName.getPid());
			props.put(TYPE, arrayElementName.getType());
			props.put(ADDRESS, arrayElementName.getAddress());
			props.put(INDEX, String.valueOf(arrayElementName.getIndex()));
			filteredModel.get(ARRAY_ELEMENTS).add(props);
		    } else if (name instanceof InstanceFieldName) {
			InstanceFieldName instanceFieldName = (InstanceFieldName) name;
			Map<String, String> props = new HashMap<>();
			props.put(PID, instanceFieldName.getPid());
			props.put(CLASSNAME, instanceFieldName.getClassName());
			props.put(OBJECT_ADDRESS, instanceFieldName.getObjectAddress());
			props.put(FIELD_NAME, instanceFieldName.getFieldName());
			filteredModel.get(INSTANCE_FIELDS).add(props);
		    } else if (name instanceof StaticFieldName) {
			StaticFieldName staticFieldName = (StaticFieldName) name;
			Map<String, String> props = new HashMap<>();
			props.put(PID, staticFieldName.getPid());
			props.put(CLASSNAME, staticFieldName.getClassName());
			props.put(FIELD_NAME, staticFieldName.getFieldName());
			filteredModel.get(STATIC_FIELDS).add(props);
		    }
		}
	    } else {
		for (JavaName name : names) {
		    if (container instanceof ObjectContainer && name instanceof ObjectName) {
			ObjectName objectName = (ObjectName) name;
			Map<String, String> props = new HashMap<>();
			props.put(PID, objectName.getPid());
			props.put(CLASSNAME, objectName.getClassName());
			props.put(ADDRESS, objectName.getAddress());
			filteredModel.get(OBJECTS).add(props);
			break;
		    } else if (container instanceof ArrayContainer && name instanceof ArrayName) {
			ArrayName arrayName = (ArrayName) name;
			Map<String, String> props = new HashMap<>();
			props.put(PID, arrayName.getPid());
			props.put(TYPE, arrayName.getType());
			props.put(ADDRESS, arrayName.getAddress());
			filteredModel.get(ARRAYS).add(props);
			break;
		    }
		}
	    }
	}
	return filteredModel;
    }
}
