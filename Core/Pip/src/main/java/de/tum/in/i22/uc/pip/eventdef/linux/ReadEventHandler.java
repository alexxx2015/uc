package de.tum.in.i22.uc.pip.eventdef.linux;

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
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 * 
 * @author Florian Kelbert
 * 
 */
public class ReadEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
//		String host = null;
//		int pid;
//		int fd;
//		String filename;
//
//		try {
//			host = getParameterValue("host");
//			pid = Integer.valueOf(getParameterValue("pid"));
//			fd = Integer.valueOf(getParameterValue("fd"));
//			filename = getParameterValue("filename");
//		} catch (ParameterNotFoundException e) {
//			_logger.error(e.getMessage());
//			return _messageFactory.createStatus(
//					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
//		}
//
//		IContainer fileCont = _informationFlowModel.getContainer(FiledescrName
//				.create(host, pid, fd));
//		if (fileCont == null) {
//			return STATUS_OKAY;
//		}
//
//		ProcessName procName = ProcessName.create(host, pid);
//		IContainer procCont = _informationFlowModel.getContainer(procName);
//		if (procCont == null) {
//			procCont = new ProcessContainer(host, pid);
//			_informationFlowModel.addName(procName, procCont);
//		}
//
//		return copyDataTransitive(fileCont, procCont);
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String host = null;
		int pid;
		int fd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer procCont = null;
		IContainer fileCont = null;
		Set<IContainer> transitiveReflexiveClosure = null;

		IStatus res = _messageFactory.createStatus(EStatus.OKAY);
		
		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			fileCont = _informationFlowModel.getContainer(FiledescrName.create(
					host, pid, fd));
			if (fileCont == null) {
				return STATUS_OKAY;
			}
		}

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			ProcessName procName = ProcessName.create(host, pid);
			procCont = _informationFlowModel.getContainer(procName);
			if (procCont == null) {
				procCont = new ProcessContainer(host, pid);
				_informationFlowModel.addName(procName, procCont);
			}
			transitiveReflexiveClosure = _informationFlowModel
					.getAliasTransitiveReflexiveClosure(procCont);

		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			// add data to transitive reflexive closure of process container
			res=copyDataTransitive(fileCont, procCont);
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
			Set<IData> dataSet = _informationFlowModel.getData(fileCont);
			IContainer intermediateCont = _informationFlowModel
					.getContainer(new NameBasic(scope.getId()));
			_informationFlowModel.addData(dataSet, intermediateCont);
		}

		return res;
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		int pid;
		int fd;
		String filename;

		try {
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of ReadFile event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck = null;
		IScope existingScope = null;
		EScopeType type = EScopeType.LOAD_FILE;

		// TEST : GENERIC BINARY APP READING THIS FILE?
		// If so behave as OUT
		attributes = new HashMap<String, Object>();
		type = EScopeType.BIN_GENERIC_LOAD;
		attributes.put("fileDescriptor", fd);
		attributes.put("pid", pid);

		scopeToCheck = new ScopeBasic("Generic binary monitored app inputing scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test succeeded. Generic binary App is reading file "
					+ filename);
			return new Pair<EBehavior, IScope>(EBehavior.OUT, existingScope);
		} else {
			_logger.debug("Test failed. Generic binary App is NOT reading file "
					+ filename);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	}

}
