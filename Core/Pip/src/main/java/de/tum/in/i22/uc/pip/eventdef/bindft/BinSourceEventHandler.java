package de.tum.in.i22.uc.pip.eventdef.bindft;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class BinSourceEventHandler extends BinDftEventHandler {

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.JBC_GENERIC_LOAD);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		try {
			String delimiter = getParameterValue("delimiter");
			String fileDescriptor = getParameterValue("fileDescriptor");
			int pid = Integer.valueOf(getParameterValue("PID"));
			String nonce = getParameterValue("nonce");
			String listOfData = getParameterValue("listOfData");

			if ((direction.equals(EBehavior.IN))
					|| (direction.equals(EBehavior.INTRAIN))) {

				IName nonceName = new NameBasic(nonce);
				IContainer nonceCnt = _informationFlowModel
						.getContainer(nonceName);

				if (nonceCnt == null) {
					nonceCnt = new ContainerBasic();
					_informationFlowModel.addName(nonceName, nonceCnt, true);
				}

				IScope os = _informationFlowModel.getOpenedScope(scope);
				if (os != null)
					scope = os;
				IName scopeName = new NameBasic(scope.getId());
				Set<IData> scopeData = _informationFlowModel.getData(scopeName);

				_informationFlowModel.addDataTransitively(scopeData, nonceCnt);
			}

			if ((direction.equals(EBehavior.OUT))
					|| (direction.equals(EBehavior.INTRAOUT))) {
				// ERROR: a sink event is never IN
				return new StatusBasic(EStatus.ERROR,
						"Error: A source event is never OUT");
			}

		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav() {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}
		delimiter = delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);
		if (scope == null)
			return Pair.of(EBehavior.UNKNOWN, null);
		if (delimiter.equals(_openDelimiter))
			return Pair.of(EBehavior.INTRA, scope);
		if (delimiter.equals(_closeDelimiter))
			return Pair.of(EBehavior.IN, scope);
		// this line should never be reached
		return Pair.of(EBehavior.UNKNOWN, null);
	}

}
