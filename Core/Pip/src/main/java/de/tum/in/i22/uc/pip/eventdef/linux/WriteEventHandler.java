package de.tum.in.i22.uc.pip.eventdef.linux;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.OSInternalName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class WriteEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd;
		String filename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}


		IName dstFdName = FiledescrName.create(host, pid, fd);

		IContainer srcCont = _informationFlowModel.getContainer(ProcessName.create(host, pid));
		IContainer dstCont = _informationFlowModel.getContainer(dstFdName);

		if (dstCont == null) {
			dstCont = _informationFlowModel.getContainer(OSInternalName.create(host, filename));

			if (dstCont == null) {
				return STATUS_ERROR;
			}

			_informationFlowModel.addName(dstFdName, dstCont);
		}

		return copyDataTransitive(srcCont, dstCont);
//		return update(EBehavior.INTRA, null);

	}
	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String host = null;
		int pid;
		int fd;
		String filename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer srcCont = null;
		IContainer dstCont = null;
		IName dstFdName = null;

		IStatus res = _messageFactory.createStatus(EStatus.OKAY);


		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			srcCont = _informationFlowModel.getContainer(ProcessName.create(host, pid));
			}

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			dstFdName = FiledescrName.create(host, pid, fd);

			dstCont = _informationFlowModel.getContainer(dstFdName);

			if (dstCont == null) {
				dstCont = _informationFlowModel.getContainer(OSInternalName.create(host, filename));

				if (dstCont == null) {
					return STATUS_ERROR;
				}

				_informationFlowModel.addName(dstFdName, dstCont);
			}
		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			res=copyDataTransitive(srcCont, dstCont);
		}

		if (direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.IN)) {
			_informationFlowModel
					.addData(_informationFlowModel.getData(new NameBasic(scope
							.getId())), dstCont);
		}

		if (direction.equals(EBehavior.INTRAOUT)
				|| direction.equals(EBehavior.OUT)) {
			IContainer dest = _informationFlowModel.getContainer(new NameBasic(
					scope.getId()));
			_informationFlowModel.addData(
					_informationFlowModel.getData(srcCont), dest);
		}

		return res;

	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		int pid;
		int fd;
		String filename = null;

		_logger.debug("XBehav function of WriteFile");

		try {
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of WriteFile event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return Pair.of(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck=null;
		IScope existingScope=null;
		EScopeType type;


		// TEST : GENERIC BINARY APP WRITING TO THIS FILE?
		// If so behave as IN
		attributes = new HashMap<String, Object>();
		type = EScopeType.BIN_GENERIC_SAVE;
		attributes.put("fileDescriptor", fd);
		attributes.put("pid", pid);
		scopeToCheck = new ScopeBasic("Generic binary app Save scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test2 succeeded. Generic binary App is writing to file "
					+ filename);
			return Pair.of(EBehavior.IN, existingScope);
		} else {
			_logger.debug("Test2 failed. Generic binary App is NOT writing to file "
					+ filename);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return Pair.of(EBehavior.INTRA, null);
	}


}
