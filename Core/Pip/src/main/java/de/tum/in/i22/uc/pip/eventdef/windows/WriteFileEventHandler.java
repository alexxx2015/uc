package de.tum.in.i22.uc.pip.eventdef.windows;

/***
 * FIXME
 * TODO
 * 
 * THIS FILE IS IN THE WRONG PACKAGE.
 * 
 * 
 * TO BE FIXED AS SOON AS TOBIAS ADDS THE "PEP" PARAMETER TO HIS UC4WIN STUFF
 * 
 * 
 * 
 * 
 */
import java.util.HashMap;
import java.util.Map;

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

public class WriteFileEventHandler extends WindowsEvents {

	public WriteFileEventHandler() {
		super();
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String fileName = null;
		String pid = null;
		// currently not used
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

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			processContainer = instantiateProcess(pid, processName);
		}

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			fileContainer = _informationFlowModel.getContainer(new NameBasic(
					fileName));

			// check if container for filename exists and create new container
			// if
			// not
			if (fileContainer == null) {
				fileContainer = _messageFactory.createContainer();
				_informationFlowModel.addName(new NameBasic(fileName),
						fileContainer, true);
			}
		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			_informationFlowModel.addData(
					_informationFlowModel.getData(processContainer),
					fileContainer);
		}

		if (direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.IN)) {
			_informationFlowModel
					.addData(_informationFlowModel.getData(new NameBasic(scope
							.getId())), fileContainer);
		}

		if (direction.equals(EBehavior.INTRAOUT)
				|| direction.equals(EBehavior.OUT)) {
			IContainer dest = _informationFlowModel.getContainer(new NameBasic(
					scope.getId()));
			_informationFlowModel.addData(
					_informationFlowModel.getData(processContainer), dest);
		}

		return _messageFactory.createStatus(EStatus.OKAY);

	}

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		String filename;
		String fileDescriptor;
		String pid;
//		String tid;
		String processName;

		_logger.debug("XBehav function of WriteFile");

		try {
			filename = getParameterValue("InFileName");
			fileDescriptor = getParameterValue("FileHandle");
			pid = getParameterValue("PID");
	//		tid = getParameterValue("TID");
			processName = getParameterValue("ProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of WriteFile event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck=null;
		IScope existingScope=null;
		EScopeType type;
		

		// TEST 1 : TB SAVING THIS FILE?
		// If so behave as IN
		if (processName.equalsIgnoreCase("Thunderbird")) {
			type = EScopeType.LOAD_FILE;
			attributes = new HashMap<String, Object>();
			attributes.put("app", "Thunderbird");
			attributes.put("filename", filename);
			scopeToCheck = new ScopeBasic("TB saving file " + filename, type,
					attributes);
			existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		}
		if (existingScope != null) {
			_logger.debug("Test1 succeeded. TB is saving to file " + filename);
			return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
		} else {
			_logger.debug("Test1 failed. TB is NOT saving to file " + filename);
		}

		// TEST 2 : IFSWebApp SAVING THIS FILE?
		// If so behave as IN
		if (processName.equalsIgnoreCase("IFSWebApp")) {
			type = EScopeType.LOAD_FILE;
			attributes = new HashMap<String, Object>();
			attributes.put("app", "IFSWebApp");
			attributes.put("filename", filename);
			scopeToCheck = new ScopeBasic("TB saving file " + filename, type,
					attributes);
			existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		}
		if (existingScope != null) {
			_logger.debug("Test2 succeeded. IFSWebApp is saving to file " + filename);
			return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
		} else {
			_logger.debug("Test2 failed. IFSWebApp is NOT saving to file " + filename);
		}
		
		// TEST 3 : GENERIC JBC APP WRITING TO THIS FILE?
		// If so behave as IN
		attributes = new HashMap<String, Object>();
		type = EScopeType.JBC_GENERIC_SAVE;
		attributes.put("fileDescriptor", fileDescriptor);
		attributes.put("pid", pid);
//		attributes.put("tid", tid);
		scopeToCheck = new ScopeBasic("Generic JBC app OUT scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test3 succeeded. Generic JBC App is writing to file "
					+ filename);
			return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
		} else {
			_logger.debug("Test3 failed. Generic JBC App is NOT writing to file "
					+ filename);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	}

}
