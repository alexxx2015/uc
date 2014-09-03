package de.tum.in.i22.uc.pip.eventdef.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ReadFileEventHandler extends WindowsEvents {

	public ReadFileEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String fileName = null;
		String pid = null;
		String processName = null;

		try {
			fileName = getParameterValue("InFileName");
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = null;
		IContainer fileContainer = null;
		Set<IContainer> transitiveReflexiveClosure = null;

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			processContainer = instantiateProcess(pid, processName);
			transitiveReflexiveClosure = _informationFlowModel
					.getAliasTransitiveReflexiveClosure(processContainer);

		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			fileContainer = _informationFlowModel.getContainer(new NameBasic(
					fileName));

			// check if container for filename exists and create new container
			// if (fileContainer == null) {
			// fileContainer = _messageFactory.createContainer();
			// _informationFlowModel.addName(new NameBasic(fileName),
			// fileContainer, true);
			// }
		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			// add data to transitive reflexive closure of process container
			Set<IData> dataSet = _informationFlowModel.getData(fileContainer);
			if (dataSet.size() > 0) {
				for (IContainer tempContainer : transitiveReflexiveClosure) {
					_informationFlowModel.addData(dataSet, tempContainer);
				}
			}
		}

		if (direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.IN)) {
			Set<IData> dataSet = _informationFlowModel.getData(new NameBasic(
					scope.getId()));
			for (IContainer tempContainer : transitiveReflexiveClosure) {
				_informationFlowModel.addData(dataSet, tempContainer);
			}
		}

		if (direction.equals(EBehavior.INTRAOUT)
				|| direction.equals(EBehavior.OUT)) {
			Set<IData> dataSet = _informationFlowModel.getData(fileContainer);
				IContainer intermediateCont = _informationFlowModel
					.getContainer(new NameBasic(scope.getId()));
			_informationFlowModel.addData(dataSet, intermediateCont);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		String filename;
		String fileDescriptor;
		String pid;
		// String tid;
		String processName;

		_logger.debug("XBehav function of ReadFile");

		try {
			filename = getParameterValue("InFileName");
			fileDescriptor = getParameterValue("FileHandle");
			pid = getParameterValue("PID");
			// tid = getParameterValue("TID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of ReadFile event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck = null;
		IScope existingScope = null;
		EScopeType type = EScopeType.LOAD_FILE;

		// TEST 1 : TB LOADING THIS FILE?
		// If so behave as OUT

		if (processName.equalsIgnoreCase("Thunderbird")) {
			attributes = new HashMap<String, Object>();
			attributes.put("app", "Thunderbird");
			attributes.put("filename", filename);
			scopeToCheck = new ScopeBasic("TB loading file " + filename, type,
					attributes);

			existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		}

		if (existingScope != null) {
			_logger.debug("Test1 succeeded. TB is loading to file " + filename);
			return new Pair<EBehavior, IScope>(EBehavior.OUT, existingScope);
		} else {
			_logger.debug("Test1 failed. TB is NOT loading to file " + filename);
		}
		
		// TEST 2 : IFSWebApp LOADING THIS FILE?
		// If so behave as OUT
		
		if (processName.equalsIgnoreCase("IFSWebApp")) {
			attributes = new HashMap<String, Object>();
			attributes.put("app", "IFSWebApp");
			//put(process ID)
			attributes.put("filename", filename);
			scopeToCheck = new ScopeBasic("IFSWebApp loading file " + filename, type,
					attributes);

			existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		}

		if (existingScope != null) {
			_logger.debug("Test2 succeeded. IFSWebApp is loading to file " + filename);
			return new Pair<EBehavior, IScope>(EBehavior.OUT, existingScope);
		} else {
			_logger.debug("Test2 failed. IFSWebApp is NOT loading to file " + filename);
		}

		// TEST 3 : GENERIC JBC APP READING THIS FILE?
		// If so behave as OUT
		attributes = new HashMap<String, Object>();
		type = EScopeType.JBC_GENERIC_LOAD;
		attributes.put("fileDescriptor", fileDescriptor);
		attributes.put("pid", pid);
		// attributes.put("tid", tid);

		scopeToCheck = new ScopeBasic("Generic JBC app inputing scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test3 succeeded. Generic JBC App is reading file "
					+ filename);
			return new Pair<EBehavior, IScope>(EBehavior.OUT, existingScope);
		} else {
			_logger.debug("Test3 failed. Generic JBC App is NOT reading file "
					+ filename);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	}

}
