package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.cm.pip.interfaces.EStateBasedFormula;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SinkEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		IStatus _return = _messageFactory.createStatus(EStatus.OKAY);
		try {
			String signature = getParameterValue("signature");
			String delimiter = getParameterValue("delimiter");
			String location = getParameterValue("location");

			String sinkId = location + _javaIFDelim + signature;
			String sourceId = iFlow.get(sinkId);

			if ((sourceId != null) && (!sourceId.equals(""))) {
				IContainer srcCnt = _informationFlowModel
						.getContainer(new NameBasic("src_" + sourceId));
				IContainer sinkCnt = _informationFlowModel
						.getContainer(new NameBasic("snk_" + sinkId));

				Set<IData> srcData = _informationFlowModel.getData(srcCnt);

				if ((direction.equals(EBehavior.INTRA))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					_informationFlowModel.addData(srcData, sinkCnt);
					Collection<IContainer> aliasSinkCnt = _informationFlowModel
							.getAliasesFrom(sinkCnt);
					Iterator<IContainer> sinkCntIt = aliasSinkCnt.iterator();
					while (sinkCntIt.hasNext()) {
						// srcData =
						// _informationFlowModel.getData(sinkCntIt.next());
						_informationFlowModel
								.addData(srcData, sinkCntIt.next());
					}
				}

				if ((direction.equals(EBehavior.IN))
						|| (direction.equals(EBehavior.INTRAIN))) {
					// ERROR: a sink event is never IN
					return new StatusBasic(EStatus.ERROR,
							"Error: A sink event is never IN");
				}

				if ((direction.equals(EBehavior.OUT))
						|| (direction.equals(EBehavior.INTRAOUT))) {
					_informationFlowModel.addDataTransitively(srcData,
							new NameBasic(scope.getId()));
				}

			}

		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	// @Override
	// protected Pair<EBehavior, IScope> XBehav(IEvent event) {
	// String filename;
	// String pid;
	// String processName;
	//
	// _logger.debug("XBehav function of WriteFile");
	//
	// try {
	// filename = getParameterValue("InFileName");
	// pid = getParameterValue("PID");
	// processName = getParameterValue("ProcessName");
	// } catch (ParameterNotFoundException e) {
	// _logger.error("Error parsing parameters of WriteFile event. falling back to default INTRA layer behavior"
	// + System.getProperty("line.separator") + e.getMessage());
	// return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	// }
	//
	// Map<String, Object> attributes;
	// IScope scopeToCheck, existingScope;
	// EScopeType type = EScopeType.LOAD_FILE;
	//
	// // TEST 1 : TB SAVING THIS FILE?
	// // If so behave as IN
	// attributes = new HashMap<String, Object>();
	// attributes.put("app", "Thunderbird");
	// attributes.put("filename", filename);
	// scopeToCheck = new ScopeBasic("TB saving file " + filename, type,
	// attributes);
	//
	// existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
	// if (existingScope != null) {
	// _logger.debug("Test1 succeeded. TB is saving to file " + filename);
	// return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
	// } else {
	// _logger.debug("Test1 failed. TB is NOT saving to file " + filename);
	// }
	//
	// // TEST 2 : GENERIC JBC APP WRITING TO THIS FILE?
	// // If so behave as IN
	// attributes = new HashMap<String, Object>();
	// type = EScopeType.JBC_GENERIC_OUT;
	// attributes.put("filename", filename);
	// scopeToCheck = new ScopeBasic("Generic JBC app OUT scope", type,
	// attributes);
	// existingScope = _informationFlowModel.getOpenedScope(scopeToCheck);
	// if (existingScope != null) {
	// _logger.debug("Test2 succeeded. Generic JBC App is writing to file "
	// + filename);
	// return new Pair<EBehavior, IScope>(EBehavior.IN, existingScope);
	// } else {
	// _logger.debug("Test2 failed. Generic JBC App is NOT writing to file "
	// + filename);
	// }
	//
	// // OTHERWISE
	// // behave as INTRA
	// _logger.debug("Any other test failed. Falling baack to default INTRA semantics");
	//
	// return new Pair<EBehavior, IScope>(EBehavior.INTRA, null);
	// }
	//
	// }
	//

}
