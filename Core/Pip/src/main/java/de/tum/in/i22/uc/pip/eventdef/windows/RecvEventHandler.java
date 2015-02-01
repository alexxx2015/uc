package de.tum.in.i22.uc.pip.eventdef.windows;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
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

public class RecvEventHandler extends WindowsEvents {

	public RecvEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String socketHandle = null;
		String processName = null;
		String pidStr = null;

		try {
			socketHandle = getParameterValue("SocketHandle");
			processName = getParameterValue("ProcessName");
			pidStr = getParameterValue("PID");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = null;
		IContainer socketContainer = null;
		Set<IContainer> transitiveReflexiveClosure = null;

		if (direction.equals(EBehavior.INTRA) || direction.equals(EBehavior.IN)
				|| direction.equals(EBehavior.INTRAIN)) {
			processContainer = instantiateProcess(pidStr, processName);
			transitiveReflexiveClosure = _informationFlowModel
					.getAliasTransitiveReflexiveClosure(processContainer);

		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.OUT)
				|| direction.equals(EBehavior.INTRAOUT)) {
			socketContainer = _informationFlowModel.getContainer(new NameBasic(
					socketHandle));

			// check if container for filename exists and create new container
			if (socketContainer == null) {
				socketContainer = _messageFactory.createContainer();
				_informationFlowModel.addName(new NameBasic(socketHandle),
						socketContainer, true);
			}
		}

		if (direction.equals(EBehavior.INTRA)
				|| direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.INTRAOUT)) {
			// add data to transitive reflexive closure of process container
			Set<IData> dataSet = _informationFlowModel.getData(socketContainer);
			for (IContainer tempContainer : transitiveReflexiveClosure) {
				_informationFlowModel.addData(dataSet, tempContainer);
			}
		}


		if (direction.equals(EBehavior.INTRAIN)
				|| direction.equals(EBehavior.IN)){
			Set<IData> dataSet = _informationFlowModel.getData(new NameBasic(
					scope.getId()));
			for (IContainer tempContainer : transitiveReflexiveClosure) {
				_informationFlowModel.addData(dataSet, tempContainer);
			}
		}

		if (direction.equals(EBehavior.INTRAOUT)
				|| direction.equals(EBehavior.OUT)){
			Set<IData> dataSet = _informationFlowModel.getData(socketContainer);
			IContainer intermediateCont = _informationFlowModel.getContainer(new NameBasic(scope.getId()));
			_informationFlowModel.addData(dataSet, intermediateCont);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}



	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		_logger.debug("XBehav function of Recv");
		String socketHandle = null;
		String pid = null;

		try {
			socketHandle = getParameterValue("SocketHandle");
			pid = getParameterValue("PID");
		} catch (ParameterNotFoundException e) {
			_logger.error("Error parsing parameters of Recv event. falling back to default INTRA layer behavior"
					+ System.getProperty("line.separator") + e.getMessage());
			return Pair.of(EBehavior.INTRA, null);
		}

		Map<String, Object> attributes;
		IScope scopeToCheck=null;
		IScope existingScope=null;
		EScopeType type;


		// TEST : GENERIC JBC APP READING FROM THIS SOCKET?
		// If so behave as OUT
		attributes = new HashMap<String, Object>();
		type = EScopeType.JBC_GENERIC_LOAD;
		attributes.put("fileDescriptor", socketHandle);
		attributes.put("pid", pid);
		scopeToCheck = new ScopeBasic("Generic JBC app IN scope", type,
				attributes);
		existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
		if (existingScope != null) {
			_logger.debug("Test2 succeeded. Generic JBC App is reading from socket "
					+ socketHandle);
			return Pair.of(EBehavior.OUT, existingScope);
		} else {
			_logger.debug("Test2 failed. Generic JBC App is NOT reading from socket "
					+ socketHandle);
		}

		// OTHERWISE
		// behave as INTRA
		_logger.debug("Any other test failed. Falling baack to default INTRA semantics");

		return Pair.of(EBehavior.INTRA, null);
	}




}
