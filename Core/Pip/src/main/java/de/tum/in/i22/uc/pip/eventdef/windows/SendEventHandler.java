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

public class SendEventHandler extends WindowsEvents {

	public SendEventHandler() {
		super();
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String socketHandle = null;
		String processHandle = null;
		// currently not used
		String processName = null;
		String pidStr = null;
		int pid=-1;
		
		try {
			socketHandle = getParameterValue("SocketHandle");
			processHandle = getParameterValue("ProcessHandle");
			processName = getParameterValue("ProcessName");
			pidStr = getParameterValue("PID");
			pid= Integer.valueOf(pidStr);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = null;
		IContainer socketContainer = null;

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			processContainer = instantiateProcess(pidStr, processName);
		}

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			socketContainer = _informationFlowModel.getContainer(new NameBasic(
					socketHandle));

			// check if container for filename exists and create new container
			// if
			// not
			if (socketContainer == null) {
				socketContainer = _messageFactory.createContainer();
				_informationFlowModel.addName(new NameBasic(socketHandle),
						socketContainer, true);
			}
		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			_informationFlowModel.addData(
					_informationFlowModel.getData(processContainer),
					socketContainer);
		}

		if (direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.IN)) {
			_informationFlowModel
					.addData(_informationFlowModel.getData(new NameBasic(scope
							.getId())), socketContainer);
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
		_logger.debug("XBehav function of ReadFile");
		String socketHandle = null;
		String processHandle = null;
		// currently not used
		//String tid = null;
		String pid = null;
		
		try {
			socketHandle = getParameterValue("SocketHandle");
			processHandle = getParameterValue("ProcessHandle");
			pid = getParameterValue("PID");
	//		tid = getParameterValue("TID");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of WriteFile event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck=null;
		IScope existingScope=null;
		EScopeType type;
		

		// TEST : GENERIC JBC APP WRITING TO THIS SOCKET?
		// If so behave as IN
		attributes = new HashMap<String, Object>();
		type = EScopeType.JBC_GENERIC_OUT;
		attributes.put("fileDescriptor", socketHandle);
		attributes.put("pid", pid);
//		attributes.put("tid", tid);
		scopeToCheck = new ScopeBasic("Generic JBC app OUT scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test2 succeeded. Generic JBC App is writing to socket "
					+ socketHandle);
			return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
		} else {
			_logger.debug("Test2 failed. Generic JBC App is NOT writing to socket "
					+ socketHandle);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	}

}
